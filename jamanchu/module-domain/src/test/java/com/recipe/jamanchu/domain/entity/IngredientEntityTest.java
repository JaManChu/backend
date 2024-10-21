package com.recipe.jamanchu.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.repository.IngredientRepository;
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
  @DisplayName("Ingredient Entity Builder test")
  void builder() {
    // given
    IngredientEntity ingredientEntity = IngredientEntity.builder()
        .ingredientId(1L)
        .ingredientName("양배추")
        .build();

    // when
    when(ingredientRepository.save(any())).thenReturn(ingredientEntity);

    // Act
    IngredientEntity savedIngredient = ingredientRepository.save(ingredientEntity);

    // then
    assertEquals(ingredientEntity.getIngredientId(), savedIngredient.getIngredientId());
    assertEquals(ingredientEntity.getIngredientName(), savedIngredient.getIngredientName());
  }

}