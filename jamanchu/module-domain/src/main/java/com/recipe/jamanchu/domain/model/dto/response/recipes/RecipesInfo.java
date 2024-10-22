package com.recipe.jamanchu.domain.model.dto.response.recipes;

import com.recipe.jamanchu.domain.model.dto.response.ingredients.Ingredient;
import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 레시피 상세 정보
 */
@Getter
@AllArgsConstructor
public class RecipesInfo {

  private final Long recipeId;

  private final String recipeAuthor;

  private final String recipeName;

  private final LevelType recipeLevel;

  private final CookingTimeType recipeCookingTime;

  private final String recipeThumbnail;

  private final List<Ingredient> recipeIngredients;

  private final List<RecipesManual> recipesManuals;

  private final Double recipeRating;
}

