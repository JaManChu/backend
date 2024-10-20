package com.recipe.jamanchu.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.repository.IngredientRepository;
import com.recipe.jamanchu.repository.RecipeIngredientRepository;
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
        .id(1L)
        .build();

    RecipeIngredientEntity recipeIngredientEntity = RecipeIngredientEntity.builder()
        .recipe(recipe)
        .name("양배추")
        .quantity("1/2개")
        .build();

    // when
    when(recipeIngredientRepository.save(any())).thenReturn(recipeIngredientEntity);

    // Act
    RecipeIngredientEntity savedIngredient = recipeIngredientRepository.save(recipeIngredientEntity);

    // then
    assertEquals(recipeIngredientEntity.getRecipe().getId(), savedIngredient.getRecipe().getId());
    assertEquals(recipeIngredientEntity.getName(), savedIngredient.getName());
    assertEquals(recipeIngredientEntity.getQuantity(), savedIngredient.getQuantity());
  }
}