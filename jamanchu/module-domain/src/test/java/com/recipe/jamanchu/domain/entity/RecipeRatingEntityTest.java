package com.recipe.jamanchu.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.model.type.UserRole;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeRatingEntityTest {

  @Mock
  private RecipeRatingRepository recipeRatingRepository;

  @Test
  @DisplayName("Recipe Rating Entity Builder Test")
  void builder() {

    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .id(1L)
        .build();

    UserEntity user = UserEntity.builder()
        .userId(1L)
        .nickname("user")
        .email("user@gmail.com")
        .password("1234")
        .role(UserRole.USER)
        .build();

    RecipeRatingEntity recipeRatingEntity = RecipeRatingEntity.builder()
        .recipe(recipe)
        .user(user)
        .rating(5.0)
        .build();

    // when
    when(recipeRatingRepository.save(recipeRatingEntity)).thenReturn(recipeRatingEntity);

    // act
    RecipeRatingEntity savedRating = recipeRatingRepository.save(recipeRatingEntity);

    // then
    assertEquals(recipeRatingEntity.getRecipe().getId(), savedRating.getRecipe().getId());
  }
}