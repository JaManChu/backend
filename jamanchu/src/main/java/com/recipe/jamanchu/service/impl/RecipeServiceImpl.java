package com.recipe.jamanchu.service.impl;

import static com.recipe.jamanchu.model.type.RecipeProvider.USER;
import static com.recipe.jamanchu.model.type.TokenType.ACCESS;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.IngredientEntity;
import com.recipe.jamanchu.entity.RecipeIngredientEntity;
import com.recipe.jamanchu.entity.ManualEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.RecipeIngredientMappingEntity;
import com.recipe.jamanchu.entity.RecipeRatingEntity;
import com.recipe.jamanchu.entity.ScrapedRecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.exceptions.exception.UnmatchedUserException;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.ingredients.Ingredient;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesInfo;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesManual;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesSummary;
import com.recipe.jamanchu.model.dto.response.recipes.RecommendRecipe;
import com.recipe.jamanchu.model.dto.response.recipes.RecommendRecipes;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.ScrapedType;
import com.recipe.jamanchu.model.type.TokenType;
import com.recipe.jamanchu.repository.IngredientRepository;
import com.recipe.jamanchu.repository.ManualRepository;
import com.recipe.jamanchu.repository.RecipeIngredientMappingRepository;
import com.recipe.jamanchu.repository.RecipeIngredientRepository;
import com.recipe.jamanchu.repository.RecipeRatingRepository;
import com.recipe.jamanchu.repository.RecipeRepository;
import com.recipe.jamanchu.repository.ScrapedRecipeRepository;
import com.recipe.jamanchu.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService {

  private final RecipeRepository recipeRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final ManualRepository manualRepository;
  private final ScrapedRecipeRepository scrapedRecipeRepository;
  private final RecipeRatingRepository recipeRatingRepository;
  private final RecipeIngredientMappingRepository recipeIngredientMappingRepository;
  private final IngredientRepository ingredientRepository;
  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;

  private final Map<Long, RecommendRecipes> recommendationCache = new ConcurrentHashMap<>();

  private final Map<Long, Map<Long, Double>> recipeDifferences = new ConcurrentHashMap<>();
  private final Map<Long, Map<Long, Integer>> recipeCounts = new ConcurrentHashMap<>();

  @Override
  @Transactional
  public ResultResponse registerRecipe(HttpServletRequest request, RecipesDTO recipesDTO) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .name(recipesDTO.getRecipeName())
        .level(recipesDTO.getRecipeLevel())
        .time(recipesDTO.getRecipeCookingTime())
        .thumbnail(String.valueOf(recipesDTO.getRecipeThumbnail()))
        .provider(USER)
        .build();

    recipeRepository.save(recipe);

    List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getRecipeIngredients().size(); i++) {
      RecipeIngredientEntity ingredient = RecipeIngredientEntity.builder()
          .recipe(recipe)
          .name(recipesDTO.getRecipeIngredients().get(i).getIngredientName())
          .quantity(recipesDTO.getRecipeIngredients().get(i).getIngredientQuantity())
          .build();

      recipeIngredientEntities.add(ingredient);
    }

    recipeIngredientRepository.saveAll(recipeIngredientEntities);

    List<IngredientEntity> ingredientEntities = new ArrayList<>();
    List<RecipeIngredientMappingEntity> recipeIngredientMappings = new ArrayList<>();
    for (RecipeIngredientEntity recipeIngredient : recipeIngredientEntities) {
      IngredientEntity ingredient = ingredientRepository.findByIngredientName(recipeIngredient.getName())
          .orElseGet(() -> {
            IngredientEntity newIngredient = IngredientEntity.builder()
                .ingredientName(recipeIngredient.getName())
                .build();
            ingredientEntities.add(newIngredient);
            return newIngredient;
          });

      RecipeIngredientMappingEntity mapping = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappings.add(mapping);
    }

    ingredientRepository.saveAll(ingredientEntities);
    recipeIngredientMappingRepository.saveAll(recipeIngredientMappings);

    List<ManualEntity> manuals = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getRecipeOrderContents().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesDTO.getRecipeOrderContents().get(i).getRecipeOrderContent())
          .manualPicture(recipesDTO.getRecipeOrderContents().get(i).getRecipeOrderImage())
          .build();

      manuals.add(manual);
    }

    manualRepository.saveAll(manuals);

    return ResultResponse.of(ResultCode.SUCCESS_REGISTER_RECIPE, recipe.getId());
  }

  @Override
  @Transactional
  public ResultResponse updateRecipe(HttpServletRequest request,
      RecipesUpdateDTO recipesUpdateDTO) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    Long recipeId = recipesUpdateDTO.getRecipeId();

    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    if (!Objects.equals(user.getUserId(), recipe.getUser().getUserId())) {
      throw new UnmatchedUserException();
    }

    recipe.updateRecipe(recipesUpdateDTO.getRecipeName(),
        recipesUpdateDTO.getRecipeLevel(), recipesUpdateDTO.getRecipeCookingTime(),
        String.valueOf(recipesUpdateDTO.getRecipeThumbnail()));

    // 기존 recipeId로 저장된 재료 삭제
    recipeIngredientMappingRepository.deleteAllByRecipeId(recipeId);
    recipeIngredientRepository.deleteAllByRecipeId(recipeId);

    List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();
    for (int i = 0; i < recipesUpdateDTO.getRecipeIngredients().size(); i++) {
      RecipeIngredientEntity ingredient = RecipeIngredientEntity.builder()
          .recipe(recipe)
          .name(recipesUpdateDTO.getRecipeIngredients().get(i).getIngredientName())
          .quantity(recipesUpdateDTO.getRecipeIngredients().get(i).getIngredientQuantity())
          .build();

      recipeIngredientEntities.add(ingredient);
    }

    recipeIngredientRepository.saveAll(recipeIngredientEntities);

    List<IngredientEntity> ingredientEntities = new ArrayList<>();
    List<RecipeIngredientMappingEntity> recipeIngredientMappings = new ArrayList<>();
    for (RecipeIngredientEntity recipeIngredient : recipeIngredientEntities) {
      IngredientEntity ingredient = ingredientRepository.findByIngredientName(recipeIngredient.getName())
          .orElseGet(() -> {
            IngredientEntity newIngredient = IngredientEntity.builder()
                .ingredientName(recipeIngredient.getName())
                .build();
            ingredientEntities.add(newIngredient);
            return newIngredient;
          });

      RecipeIngredientMappingEntity mapping = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappings.add(mapping);
    }

    ingredientRepository.saveAll(ingredientEntities);
    recipeIngredientMappingRepository.saveAll(recipeIngredientMappings);

    manualRepository.deleteAllByRecipeId(recipeId);

    List<ManualEntity> manuals = new ArrayList<>();
    for (int i = 0; i < recipesUpdateDTO.getRecipeOrderContents().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesUpdateDTO.getRecipeOrderContents().get(i).getRecipeOrderContent())
          .manualPicture(recipesUpdateDTO.getRecipeOrderContents().get(i).getRecipeOrderImage())
          .build();

      manuals.add(manual);
    }

    manualRepository.saveAll(manuals);

    return ResultResponse.of(ResultCode.SUCCESS_UPDATE_RECIPE);
  }

  @Override
  @Transactional
  public ResultResponse deleteRecipe(HttpServletRequest request,
      RecipesDeleteDTO recipesDeleteDTO) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    Long recipeId = recipesDeleteDTO.getRecipeId();

    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    if (!Objects.equals(user.getUserId(), recipe.getUser().getUserId())) {
      throw new UnmatchedUserException();
    }

    recipeRepository.deleteById(recipeId);

    return ResultResponse.of(ResultCode.SUCCESS_DELETE_RECIPE);
  }

  @Override
  public ResultResponse getRecipes(HttpServletRequest request, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    List<Long> scrapedRecipeIds = getScrapedRecipeIds(request);

    // 만약 scrapedRecipeIds가 비어있지 않다면 SCRAPED한 레시피를 제외한 나머지 레시피 조회
    Page<RecipeEntity> recipes;
    if (!scrapedRecipeIds.isEmpty()) {
      recipes = recipeRepository.findByIdNotIn(scrapedRecipeIds, pageable);
    } else {
      // SCRAPED한 레시피가 없거나 Token이 없으면 모든 레시피를 조회
      recipes = recipeRepository.findAll(pageable);
    }

    // 레시피가 없는 경우 예외 처리
    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = convertToRecipesSummary(recipes);

    // 결과 반환
    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_ALL_RECIPES, recipesSummaries);
  }

  @Override
  public ResultResponse searchRecipes(HttpServletRequest request, RecipesSearchDTO recipesSearchDTO,
      int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    List<Long> scrapedRecipeIds = getScrapedRecipeIds(request);

    Page<RecipeEntity> recipes = searchAndRecipes(recipesSearchDTO, scrapedRecipeIds, pageable);

    if (recipes.isEmpty()) {
      recipes = searchOrRecipes(recipesSearchDTO, scrapedRecipeIds, pageable);
    }

    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = convertToRecipesSummary(recipes);

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECIPES, recipesSummaries);
  }

  private Page<RecipeEntity> searchAndRecipes(RecipesSearchDTO recipesSearchDTO,
      List<Long> scrapedRecipeIds, Pageable pageable) {
    if (!scrapedRecipeIds.isEmpty()) {
      return recipeRepository.searchAndRecipesIdNotIn(
          recipesSearchDTO.getRecipeLevel(),
          recipesSearchDTO.getRecipeCookingTime(),
          recipesSearchDTO.getIngredients(),
          (long) recipesSearchDTO.getIngredients().size(),
          scrapedRecipeIds,
          pageable
      );
    } else {
      // SCRAPED한 레시피가 없거나 Token이 없으면 And 조건으로 레시피를 조회
      return recipeRepository.searchAndRecipes(
          recipesSearchDTO.getRecipeLevel(),
          recipesSearchDTO.getRecipeCookingTime(),
          recipesSearchDTO.getIngredients(),
          (long) recipesSearchDTO.getIngredients().size(),
          pageable
      );
    }
  }

  private Page<RecipeEntity> searchOrRecipes(RecipesSearchDTO recipesSearchDTO,
      List<Long> scrapedRecipeIds, Pageable pageable) {
    if (!scrapedRecipeIds.isEmpty()) {
      return recipeRepository.searchOrRecipesIdNotIn(
          recipesSearchDTO.getRecipeLevel(),
          recipesSearchDTO.getRecipeCookingTime(),
          recipesSearchDTO.getIngredients(),
          scrapedRecipeIds,
          pageable
      );
    } else {
      // SCRAPED한 레시피가 없거나 Token이 없으면 Or 조건으로 레시피를 조회
      return recipeRepository.searchOrRecipes(
          recipesSearchDTO.getRecipeLevel(),
          recipesSearchDTO.getRecipeCookingTime(),
          recipesSearchDTO.getIngredients(),
          pageable
      );
    }
  }

  @Override
  public ResultResponse getRecipeDetail(Long recipeId) {
    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    List<Ingredient> ingredients = recipe.getIngredients().stream()
        .map(ingredient -> new Ingredient(
            ingredient.getName(),
            ingredient.getQuantity()
        ))
        .toList();

    List<RecipesManual> recipesManuals = recipe.getManuals().stream()
        .map(manual -> new RecipesManual(
            manual.getManualContent(),
            manual.getManualPicture()
        ))
        .toList();

    RecipesInfo recipesInfo = new RecipesInfo(
        recipe.getId(),
        recipe.getUser().getNickname(),
        recipe.getName(),
        recipe.getLevel(),
        recipe.getTime(),
        recipe.getThumbnail(),
        ingredients,
        recipesManuals,
        Optional.ofNullable(recipeRatingRepository.findAverageRatingByRecipeId(recipe.getId()))
            .orElse(0.0)
    );

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECIPES_DETAILS, recipesInfo);
  }

  @Override
  public ResultResponse getRecipesByRating(HttpServletRequest request, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    List<Long> scrapedRecipeIds = getScrapedRecipeIds(request);

    // 만약 scrapedRecipeIds가 비어있지 않다면 SCRAPED한 레시피를 제외한 나머지 레시피를 평점순으로 조회
    Page<RecipeEntity> recipes;
    if (!scrapedRecipeIds.isEmpty()) {
      recipes = recipeRepository.findByIdNotInOrderByRating(scrapedRecipeIds, pageable);
    } else {
      // SCRAPED한 레시피가 없거나 Token이 없으면 모든 레시피를 평점순으로 조회
      recipes = recipeRepository.findAllOrderByRating(pageable);
    }

    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = convertToRecipesSummary(recipes);

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_ALL_RECIPES_BY_RATING, recipesSummaries);
  }

  @Override
  public ResultResponse scrapedRecipe(HttpServletRequest request, Long recipeId) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    ScrapedRecipeEntity scrapedRecipe = scrapedRecipeRepository.findByUserAndRecipe(user, recipe);

    if (scrapedRecipe != null) {
      scrapedRecipe.updateScrapedType(scrapedRecipe.getScrapedType() == ScrapedType.SCRAPED
          ? ScrapedType.CANCELED
          : ScrapedType.SCRAPED);
    } else {
      scrapedRecipe = ScrapedRecipeEntity.builder()
          .user(user)
          .recipe(recipe)
          .scrapedType(ScrapedType.SCRAPED)
          .build();
    }

    scrapedRecipeRepository.save(scrapedRecipe);

    return ResultResponse.of(
        scrapedRecipe.getScrapedType() == ScrapedType.SCRAPED
            ? ResultCode.SUCCESS_SCRAPED_RECIPE
            : ResultCode.SUCCESS_CANCELED_SCRAP_RECIPE, scrapedRecipe.getScrapedType());
  }


  @Override
  public ResultResponse getRecommendRecipes(HttpServletRequest request) {

    // 유저 아이디 가져오기
    Long userId = jwtUtil.getUserId(request.getHeader(ACCESS.getValue()));

    // 유저 확인
    UserEntity user = userAccessHandler.findByUserId(userId);

    // 레시피 평가 항목이 있는지 조회
    if (!recipeRatingRepository.existsByUser(user)) {
      // 분기) 레시피 평가 항목이 없을 경우 -> 기존 레시피에서 평균 평점이 제일 높은 레시피 추천
      return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECOMMEND_RECIPES,
          getRecommendRecipesWhenNoRating());
    } else {
      // 분기) 레시피 평가 항목이 있는 경우 -> 유저의 평가 항목을 기반으로 레시피 추천
      return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECOMMEND_RECIPES,
          getRecommendRecipesWhenRating(user.getUserId()));
    }
  }

  /*
   * 레시피 평가 항목이 없을 경우 -> 기존 레시피에서 평균 평점이 제일 높은 레시피 추천
   */
  private RecommendRecipes getRecommendRecipesWhenNoRating() {
    return RecommendRecipes.of(
        recipeRatingRepository.findThreePopularRecipe()
            .stream()
            .map(recipe -> {
              UserEntity user = recipe.getUser();
              return RecommendRecipe.of(recipe, user);
            })
            .collect(Collectors.toList())
    );
  }

  /*
   * 레시피 평가 항목이 있는 경우 -> 유저의 평가 항목을 기반으로 레시피 추천
   */
  public RecommendRecipes getRecommendRecipesWhenRating(Long userId) {

    if (recommendationCache.get(userId).getRecipes().isEmpty()
        || recipeRatingRepository.findAll().size() < 30) {
      return getRecommendRecipesWhenNoRating();
    }
    return recommendationCache.getOrDefault(userId, RecommendRecipes.empty());
  }

  /*
   * 모든 유저에 대해서 추천 레시피 계산
   */
  @Override
  public void calculateAllRecommendations() {

    computeDifferences();

    List<UserEntity> users = userAccessHandler.findAllUsers();

    for (UserEntity user : users) {
      RecommendRecipes recommendations = this.recommend(user);
      recommendationCache.put(user.getUserId(), recommendations);
    }
  }

  /*
   * 레시피 간의 차이 계산
   */
  private void computeDifferences() {

    // 모든 레시피 평가 데이터 조회
    List<RecipeRatingEntity> ratings = recipeRatingRepository.findAll();

    // (User, Recipe) 데이터를 User 단위로 그룹화
    Map<UserEntity, List<RecipeRatingEntity>> userRatings = ratings.stream()
        .collect(Collectors.groupingBy(RecipeRatingEntity::getUser));

    // 모든 유저의 평가 데이터에 대해 반복
    for (List<RecipeRatingEntity> userRating : userRatings.values()) {
      for (int i = 0; i < userRating.size(); i++) {
        for (int j = i + 1; j < userRating.size(); j++) {
          RecipeRatingEntity r1 = userRating.get(i);
          RecipeRatingEntity r2 = userRating.get(j);

          // RecipeA, RecipeB 쌍
          long recipeA = r1.getRecipe().getId();
          long recipeB = r2.getRecipe().getId();

          double diff = r1.getRating() - r2.getRating();

          // RecipeA와 RecipeB 간의 차이 저장
          recipeDifferences.putIfAbsent(recipeA, new ConcurrentHashMap<>());
          recipeDifferences.get(recipeA).put(recipeB,
              recipeDifferences.get(recipeA).getOrDefault(recipeB, 0.0) + diff);

          recipeCounts.putIfAbsent(recipeA, new ConcurrentHashMap<>());
          recipeCounts.get(recipeA).put(recipeB,
              recipeCounts.get(recipeA).getOrDefault(recipeB, 0) + 1);
        }
      }
    }

    // 평균 차이 계산
    for (long recipeA : recipeDifferences.keySet()) {
      for (long recipeB : recipeDifferences.get(recipeA).keySet()) {
        double oldValue = recipeDifferences.get(recipeA).get(recipeB);
        int count = recipeCounts.get(recipeA).get(recipeB);
        recipeDifferences.get(recipeA).put(recipeB, oldValue / count);
      }
    }
  }

  public RecommendRecipes recommend(UserEntity user) {
    Map<Long, Double> recommendations = new HashMap<>();
    Map<Long, Integer> frequencies = new HashMap<>();

    List<RecipeRatingEntity> userRatings = recipeRatingRepository.findByUser(user);

    for (RecipeRatingEntity rating : userRatings) {
      long recipeId = rating.getRecipe().getId();

      // 사용자가 평가한 레시피와 차이를 계산하여 추천 점수 갱신
      for (Map.Entry<Long, Double> entry : recipeDifferences.getOrDefault(recipeId,
          Collections.emptyMap()).entrySet()) {
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
    int topN = 3;
    return RecommendRecipes.of(
        recommendations.entrySet()
            .stream()
            .filter(e -> e.getValue() >= 4.0)
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .limit(topN)
            .map(e -> RecommendRecipe.of(
                recipeRepository.findById(e.getKey()).orElseThrow(RecipeNotFoundException::new),
                user))
            .toList()
    );
  }

  // 유저의 scrapedRecipeIds를 가져오는 메서드
  private List<Long> getScrapedRecipeIds(HttpServletRequest request) {
    Long userId;
    List<Long> scrapedRecipeIds = Collections.emptyList();
    String token = request.getHeader(TokenType.ACCESS.getValue());
    if (token != null) {
      userId = jwtUtil.getUserId(token);
    } else {
      return scrapedRecipeIds;
    }

    if (userId != null) {
      scrapedRecipeIds = scrapedRecipeRepository.findRecipeIdsByUserIdAndScrapedType(userId,
          ScrapedType.SCRAPED);
    }

    return scrapedRecipeIds;
  }

  // RecipeEntity 리스트를 RecipesSummary로 변환하는 메서드
  private List<RecipesSummary> convertToRecipesSummary(Page<RecipeEntity> recipes) {
    return recipes.stream().map(
        recipeEntity -> new RecipesSummary(
            recipeEntity.getId(),
            recipeEntity.getName(),
            recipeEntity.getUser().getNickname(),
            recipeEntity.getLevel(),
            recipeEntity.getTime(),
            recipeEntity.getThumbnail(),
            Optional.ofNullable(
                    recipeRatingRepository.findAverageRatingByRecipeId(recipeEntity.getId()))
                .orElse(0.0)
        )
    ).toList();
  }
}
