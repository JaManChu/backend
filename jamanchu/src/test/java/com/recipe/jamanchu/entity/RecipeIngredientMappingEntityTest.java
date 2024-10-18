package com.recipe.jamanchu.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.repository.RecipeIngredientMappingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeIngredientMappingEntityTest {

  @Mock
  private RecipeIngredientMappingRepository recipeIngredientMappingRepository;

  @Test
  @DisplayName("Recipe Ingredient Mapping Entity Builder Test")
  void builder() {

    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .id(1L)
        .build();

    IngredientEntity ingredient = IngredientEntity.builder()
        .ingredientId(1L)
        .ingredientName("ingredient")
        .build();

    RecipeIngredientMappingEntity recipeIngredientMappingEntity = RecipeIngredientMappingEntity.builder()
        .recipe(recipe)
        .ingredient(ingredient)
        .build();

    // when
    when(recipeIngredientMappingRepository.save(recipeIngredientMappingEntity)).thenReturn(recipeIngredientMappingEntity);

    // act
    RecipeIngredientMappingEntity savedMapping = recipeIngredientMappingRepository.save(recipeIngredientMappingEntity);

    // then
    assertEquals(recipeIngredientMappingEntity.getRecipe().getId(), savedMapping.getRecipe().getId());
  }
}