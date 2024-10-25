package com.recipe.jamanchu.batch.recommend;

import com.recipe.jamanchu.batch.recommend.schedule.RecommendRecipeCountsMap;
import com.recipe.jamanchu.batch.recommend.schedule.RecommendRecipeDifferencesMap;
import com.recipe.jamanchu.core.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeRatingEntity;
import com.recipe.jamanchu.domain.entity.RecommendRecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import com.recipe.jamanchu.domain.repository.RecommendRecipeRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendCalculate {

  private final RecipeRepository recipeRepository;

  private final RecipeRatingRepository recipeRatingRepository;

  private final UserAccessHandler userAccessHandler;

  private final RecommendRecipeRepository recommendRecipeRepository;

  private final RecommendRecipeDifferencesMap recipeDifferences;

  private final RecommendRecipeCountsMap recipeCounts;

  /*
   * 모든 유저에 대해서 추천 레시피 계산
   */
  @Transactional
  public void calculateAllRecommendations() {

    // 평점 데이터 전체 조회
    List<RecipeRatingEntity> ratings = recipeRatingRepository.findAll();

    // 유저와 평가 데이터 그룹화
    Map<UserEntity, List<RecipeRatingEntity>> userRatings = ratings.stream()
        .collect(Collectors.groupingBy(RecipeRatingEntity::getUser));

    /* 추후 삭제 예정 */
    /* 유저가 어떻게 평가했는지에 대한 로그 출력 */
    userRatings.forEach((user, ratings2) -> {
      StringBuilder ratingLog = new StringBuilder();
      ratingLog.append("User: ").append(user.getUserId()).append(", Ratings: ");

      ratings2.forEach(rating -> {
        ratingLog.append("[Recipe: ")
            .append(rating.getRecipe().getId())
            .append(", Rating: ")
            .append(rating.getRating())
            .append("], ");
      });

      // 최종 로그 출력
      log.info(ratingLog.toString());
    });

    // 모든 유저의 평가 데이터에 대해 반복
    for (List<RecipeRatingEntity> userRating : userRatings.values()) {

      // 투포인터 알고리즘을 사용하여 레시피 간의 차이 계산
      for (int i = 0; i < userRating.size(); i++) {
        for (int j = i + 1; j < userRating.size(); j++) {

          // 1번 2번, 1번 3번, .... -> 1번 레시피에 대한 차이 계산
          RecipeRatingEntity r1 = userRating.get(i);
          RecipeRatingEntity r2 = userRating.get(j);

          // RecipeA, RecipeB 쌍
          long recipeA = r1.getRecipe().getId();
          long recipeB = r2.getRecipe().getId();

          double diff = r1.getRating() - r2.getRating();

          // RecipeA와 RecipeB 간의 차이 저장
          recipeDifferences.putIfAbsent(recipeA, new ConcurrentHashMap<>());
          Map<Long, Double> recipeADiffs = recipeDifferences.get(recipeA);
          if (recipeADiffs != null) {
            recipeADiffs.put(recipeB, recipeADiffs.getOrDefault(recipeB, 0.0) + diff);
          }

          recipeCounts.putIfAbsent(recipeA, new ConcurrentHashMap<>());
          Map<Long, Integer> recipeACounts = recipeCounts.get(recipeA);
          if (recipeACounts != null) {
            recipeACounts.put(recipeB, recipeACounts.getOrDefault(recipeB, 0) + 1);
          }
        }
      }
    }

    /* 추후 삭제예정 */
    /* 각 레시피별 편차 계산 로그 출력 */
    recipeDifferences.forEach((recipeA, differences) -> {
      StringBuilder diffLog = new StringBuilder();
      diffLog.append("RecipeA: ").append(recipeA).append(", Differences: ");

      differences.forEach((recipeB, diff) -> diffLog.append("[RecipeB: ")
          .append(recipeB)
          .append(", Difference: ")
          .append(diff)
          .append("], "));

      // 최종 로그 출력
      log.info(diffLog.toString());
    });

    for (long recipeA : recipeDifferences.keySet()) {
      for (long recipeB : recipeDifferences.get(recipeA).keySet()) {
        double oldValue = recipeDifferences.get(recipeA).get(recipeB);
        int count = recipeCounts.get(recipeA).get(recipeB);
        recipeDifferences.get(recipeA).put(recipeB, oldValue / count);
      }
    }

    userAccessHandler.findAllUsers().forEach(user -> {
      Map<Long, Double> recommendations = new HashMap<>();
      Map<Long, Integer> frequencies = new HashMap<>();

      List<RecipeRatingEntity> innerUserRatings = recipeRatingRepository.findByUser(user);

      for (RecipeRatingEntity rating : innerUserRatings) {
        long recipeId = rating.getRecipe().getId();

        // 사용자가 평가한 레시피와 차이를 계산하여 추천 점수 갱신

        for (Map.Entry<Long, Double> entry : recipeDifferences.getOrDefault(recipeId,
            new ConcurrentHashMap<>()).entrySet()) {
          long otherRecipeId = entry.getKey();
          double diff = entry.getValue();

          recommendations.put(otherRecipeId,
              recommendations.getOrDefault(otherRecipeId, 0.0) + (diff + rating.getRating()));
          frequencies.put(otherRecipeId,
              frequencies.getOrDefault(otherRecipeId, 0) + 1);
        }
      }
      // 평균 계산
      recommendations.replaceAll((i, v) -> recommendations.get(i) / frequencies.get(i));

      // 추천 점수가 4.0 이상인 레시피를 평점순으로 정렬하고, 가장 높은 3개만 추천
      // 3개는 변경될 수 있음.
      int topN = 3;
      recommendations.entrySet()
          .stream()
          .filter(e -> e.getValue() >= 4.0)
          .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
          .limit(topN)
          .forEachOrdered(e -> {
                RecipeEntity recipe = recipeRepository.findById(e.getKey())
                    .orElseThrow(RecipeNotFoundException::new);
                recommendRecipeRepository.save(RecommendRecipeEntity.builder()
                    .user(user)
                    .recipe(recipe)
                    .rating(e.getValue())
                    .build());
              }
          );
    });
  }
}
