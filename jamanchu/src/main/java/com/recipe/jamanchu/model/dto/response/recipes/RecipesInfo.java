package com.recipe.jamanchu.model.dto.response.recipes;

import com.recipe.jamanchu.model.dto.response.comments.Comments;
import com.recipe.jamanchu.model.dto.response.ingredients.IngredientCoupang;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
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

  private final List<IngredientCoupang> ingredients;

  private final RecipesManuals recipesManuals;

  private final Comments comments;
}

