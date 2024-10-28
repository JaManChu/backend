package com.recipe.jamanchu.domain.entity;

import static com.recipe.jamanchu.domain.model.type.CookingTimeType.*;
import static com.recipe.jamanchu.domain.model.type.LevelType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.repository.TenThousandRecipeRepository;
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
        .trId(1L)
        .trName("TenThousandRecipeName")
        .trOriginId(1L)
        .trLevel(LOW)
        .trCookTime(TEN_MINUTES)
        .trRating(4.50)
        .trThumbnail("thumbnail")
        .trIngredients("ingredients")
        .trMnContents("contents")
        .trMnPictures("pictures")
        .build();

    // when
    when(tenThousandRecipeRepository.save(recipeEntity)).thenReturn(recipeEntity);

    // act
    TenThousandRecipeEntity savedRecipe = tenThousandRecipeRepository.save(recipeEntity);

    // then
    assertEquals(recipeEntity.getTrId(), savedRecipe.getTrId());
    assertEquals(recipeEntity.getTrName(), savedRecipe.getTrName());
    assertEquals(recipeEntity.getTrOriginId(), savedRecipe.getTrOriginId());
    assertEquals(recipeEntity.getTrLevel(), savedRecipe.getTrLevel());
    assertEquals(recipeEntity.getTrCookTime(), savedRecipe.getTrCookTime());
    assertEquals(recipeEntity.getTrRating(), savedRecipe.getTrRating());
    assertEquals(recipeEntity.getTrThumbnail(), savedRecipe.getTrThumbnail());
    assertEquals(recipeEntity.getTrIngredients(), savedRecipe.getTrIngredients());
    assertEquals(recipeEntity.getTrMnContents(), savedRecipe.getTrMnContents());
    assertEquals(recipeEntity.getTrMnPictures(), savedRecipe.getTrMnPictures());
  }
}