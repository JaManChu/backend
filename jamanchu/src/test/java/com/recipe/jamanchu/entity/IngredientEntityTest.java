package com.recipe.jamanchu.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.repository.IngredientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngredientEntityTest {

  @Mock
  private IngredientRepository ingredientRepository;

  @Test
  @DisplayName("Ingredient Entity Builder Test")
  void builder() {

    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .id(1L)
        .build();

    IngredientEntity ingredientEntity = IngredientEntity.builder()
        .recipe(recipe)
        .name("양배추")
        .quantity("1/2개")
        .build();

    // when
    when(ingredientRepository.save(any())).thenReturn(ingredientEntity);

    // Act
    IngredientEntity savedIngredient = ingredientRepository.save(ingredientEntity);

    // then
    assertEquals(ingredientEntity.getRecipe().getId(), savedIngredient.getRecipe().getId());
    assertEquals(ingredientEntity.getName(), savedIngredient.getName());
    assertEquals(ingredientEntity.getQuantity(), savedIngredient.getQuantity());
  }
}