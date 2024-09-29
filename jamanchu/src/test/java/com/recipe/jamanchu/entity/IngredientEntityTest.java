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
    String ingredientName = "양배추";

    IngredientEntity ingredientEntity = IngredientEntity.builder()
        .name(ingredientName)
        .build();

    // when
    when(ingredientRepository.save(any())).thenReturn(ingredientEntity);

    // Act
    IngredientEntity savedIngredient = ingredientRepository.save(ingredientEntity);

    // then
    assertEquals(savedIngredient.getName(), ingredientEntity.getName());
  }
}