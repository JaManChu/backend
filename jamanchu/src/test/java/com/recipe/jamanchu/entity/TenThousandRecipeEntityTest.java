package com.recipe.jamanchu.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.repository.TenThousandRecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenThousandRecipeEntityTest {

  @Mock
  private TenThousandRecipeRepository tenThousandRecipeRepository;

  @Test
  @DisplayName("TenThousandRecipe Entity Builder Test")
  void builder() {

    // given
    TenThousandRecipeEntity recipeEntity = TenThousandRecipeEntity.builder()
        .crawledRecipeId(1L)
        .name("TenThousandRecipeName")
        .levelType(LevelType.LOW)
        .cookingTimeType(CookingTimeType.TEN_MINUTES)
        .rating(4.50)
        .thumbnail("thumbnail")
        .ingredients("ingredients")
        .crManualContents("contents")
        .crManualPictures("pictures")
        .build();

    // when
    when(tenThousandRecipeRepository.save(recipeEntity)).thenReturn(recipeEntity);

    // act
    TenThousandRecipeEntity savedRecipe = tenThousandRecipeRepository.save(recipeEntity);

    // then
    assertEquals(1, savedRecipe.getCrawledRecipeId());
    assertEquals("TenThousandRecipeName", savedRecipe.getName());
    assertEquals(LevelType.LOW, savedRecipe.getLevelType());
    assertEquals(CookingTimeType.TEN_MINUTES, savedRecipe.getCookingTimeType());
    assertEquals(4.50, savedRecipe.getRating());
    assertEquals("thumbnail", savedRecipe.getThumbnail());
    assertEquals("ingredients", savedRecipe.getIngredients());
    assertEquals("contents", savedRecipe.getCrManualContents());
    assertEquals("pictures", savedRecipe.getCrManualPictures());
  }
}