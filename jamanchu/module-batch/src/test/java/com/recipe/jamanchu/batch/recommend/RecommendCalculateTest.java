package com.recipe.jamanchu.batch.recommend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.batch.recommend.schedule.RecommendRecipeCountsMap;
import com.recipe.jamanchu.batch.recommend.schedule.RecommendRecipeDifferencesMap;
import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeRatingEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import com.recipe.jamanchu.domain.repository.RecommendRecipeRepository;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendCalculateTest {

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private RecipeRatingRepository recipeRatingRepository;

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private RecommendRecipeRepository recommendRecipeRepository;

  @Mock
  private RecommendRecipeCountsMap recipeCounts;

  @Mock
  private RecommendRecipeDifferencesMap recipeDifferences;

  @InjectMocks
  private RecommendCalculate recommendCalculate;

  @DisplayName("모든 유저에 대한 추천 레시피 계산 테스트")
  @Test
  void testCalculateAllRecommendations() {
    // Arrange
    // Create mock users
    UserEntity userA = UserEntity.builder()
        .userId(1L)
        .build();

    UserEntity userB = UserEntity.builder()
        .userId(2L)
        .build();

    UserEntity userC = UserEntity.builder()
        .userId(3L)
        .build();

    // Create mock recipes
    RecipeEntity recipe1 = RecipeEntity.builder()
        .id(101L)
        .build();

    RecipeEntity recipe2 = RecipeEntity.builder()
        .id(102L)
        .build();

    RecipeEntity recipe3 = RecipeEntity.builder()
        .id(103L)
        .build();

    RecipeEntity recipe4 = RecipeEntity.builder()
        .id(104L)
        .build();

    // Create mock ratings
    RecipeRatingEntity rating1 = RecipeRatingEntity.builder()
        .user(userA)
        .recipe(recipe1)
        .rating(4.5)
        .build();

    RecipeRatingEntity rating2 = RecipeRatingEntity.builder()
        .user(userA)
        .recipe(recipe2)
        .rating(2.0)
        .build();

    RecipeRatingEntity rating3 = RecipeRatingEntity.builder()
        .user(userA)
        .recipe(recipe3)
        .rating(5.0)
        .build();

    RecipeRatingEntity rating4 = RecipeRatingEntity.builder()
        .user(userA)
        .recipe(recipe4)
        .rating(4.5)
        .build();
    RecipeRatingEntity rating5 = RecipeRatingEntity.builder()
        .user(userB)
        .recipe(recipe1)
        .rating(3.5)
        .build();

    RecipeRatingEntity rating6 = RecipeRatingEntity.builder()
        .user(userB)
        .recipe(recipe2)
        .rating(5.0)
        .build();

    RecipeRatingEntity rating7 = RecipeRatingEntity.builder()
        .user(userB)
        .recipe(recipe4)
        .rating(4.5)
        .build();

    RecipeRatingEntity rating8 = RecipeRatingEntity.builder()
        .user(userC)
        .recipe(recipe2)
        .rating(4.5)
        .build();

    RecipeRatingEntity rating9 = RecipeRatingEntity.builder()
        .user(userC)
        .recipe(recipe4)
        .rating(4.5)
        .build();

    // Mock repository methods
    when(recipeRatingRepository.findAll()).thenReturn(
        Arrays.asList(rating1, rating2, rating3, rating4, rating5, rating6, rating7, rating8,
            rating9));
    when(userAccessHandler.findAllUsers()).thenReturn(Arrays.asList(userA, userB, userC));
    when(recipeRatingRepository.findByUser(userA)).thenReturn(
        Arrays.asList(rating1, rating2, rating3, rating4));
    when(recipeRatingRepository.findByUser(userB)).thenReturn(
        Arrays.asList(rating5, rating6, rating7));
    when(recipeRatingRepository.findByUser(userC)).thenReturn(Arrays.asList(rating8, rating9));

//    when(recipeRepository.findById(anyLong())).thenAnswer(invocation -> {
//      Long id = invocation.getArgument(0);
//      if (id.equals(101L)) return Optional.of(recipe1);
//      if (id.equals(102L)) return Optional.of(recipe2);
//      if (id.equals(103L)) return Optional.of(recipe3);
//      if (id.equals(104L)) return Optional.of(recipe4);
//      else return Optional.empty();
//    });

    // Act
    recommendCalculate.calculateAllRecommendations();

    verify(recipeRatingRepository, times(1)).findAll();
    verify(recipeDifferences, times(10)).putIfAbsent(anyLong(), anyMap());
    verify(recipeDifferences, times(10)).get(anyLong());
    verify(recipeDifferences, times(1)).keySet();
    verify(recipeCounts, times(10)).putIfAbsent(anyLong(), anyMap());
    verify(recipeCounts, times(10)).get((RecipeEntity) any());
  }
}