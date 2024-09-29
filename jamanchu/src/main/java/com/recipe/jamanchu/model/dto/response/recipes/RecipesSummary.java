package com.recipe.jamanchu.model.dto.response.recipes;

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

  private final String recipeThumbnail;

}

