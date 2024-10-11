package com.recipe.jamanchu.model.dto.response.recipes;

import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 레시피 요약
 */
@Getter
@AllArgsConstructor
public class RecipesSummary {

  private final Long recipeId;

  private final String recipeName;

  private final String recipeAuthor;

  private final LevelType recipeLevel;

  private final CookingTimeType recipeCookingTime;

  private final String recipeThumbnail;

}

