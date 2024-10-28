package com.recipe.jamanchu.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.repository.RecipeIngredientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeIngredientEntityTest {

  @Mock
  private RecipeIngredientRepository recipeIngredientRepository;

  @Test
  @DisplayName("RecipeIngredient Entity Builder Test")
  void builder() {

    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .rcpId(1L)
        .build();

    RecipeIngredientEntity recipeIngredientEntity = RecipeIngredientEntity.builder()
        .recipe(recipe)
        .riName("양배추")
        .riQuantity("1/2개")
        .build();

    // when
    when(recipeIngredientRepository.save(any())).thenReturn(recipeIngredientEntity);

    // Act
    RecipeIngredientEntity savedIngredient = recipeIngredientRepository.save(recipeIngredientEntity);

    // then
    assertEquals(recipeIngredientEntity.getRecipe().getRcpId(), savedIngredient.getRecipe().getRcpId());
    assertEquals(recipeIngredientEntity.getRiName(), savedIngredient.getRiName());
    assertEquals(recipeIngredientEntity.getRiQuantity(), savedIngredient.getRiQuantity());
  }
}