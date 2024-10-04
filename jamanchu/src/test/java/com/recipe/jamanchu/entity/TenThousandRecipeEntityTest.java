package com.recipe.jamanchu.entity;

import static com.recipe.jamanchu.model.type.CookingTimeType.*;
import static com.recipe.jamanchu.model.type.LevelType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
        .levelType(LOW)
        .cookingTimeType(TEN_MINUTES)
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
    assertEquals(recipeEntity.getCrawledRecipeId(), savedRecipe.getCrawledRecipeId());
    assertEquals(recipeEntity.getName(), savedRecipe.getName());
    assertEquals(recipeEntity.getLevelType(), savedRecipe.getLevelType());
    assertEquals(recipeEntity.getCookingTimeType(), savedRecipe.getCookingTimeType());
    assertEquals(recipeEntity.getRating(), savedRecipe.getRating());
    assertEquals(recipeEntity.getThumbnail(), savedRecipe.getThumbnail());
    assertEquals(recipeEntity.getIngredients(), savedRecipe.getIngredients());
    assertEquals(recipeEntity.getCrManualContents(), savedRecipe.getCrManualContents());
    assertEquals(recipeEntity.getCrManualPictures(), savedRecipe.getCrManualPictures());
  }
}