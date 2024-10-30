package com.recipe.jamanchu.domain.model.dto.response.recipes;

import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeRatingEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
import lombok.Getter;

@Getter
public class RecommendRecipe {

  private final Long recipeId;

  private final String recipeName;

  private final String recipeAuthor;

  private final String recipeThumbnail;

  private final Double recipeRating;

  private final LevelType recipeLevel;

  private final CookingTimeType recipeCookingTime;

  private RecommendRecipe(Long recipeId, String recipeName, String recipeAuthor, String recipeThumbnail, Double recipeRating, LevelType recipeLevel, CookingTimeType recipeCookingTime) {
    this.recipeId = recipeId;
    this.recipeName = recipeName;
    this.recipeAuthor = recipeAuthor;
    this.recipeThumbnail = recipeThumbnail;
    this.recipeRating = recipeRating;
    this.recipeLevel = recipeLevel;
    this.recipeCookingTime = recipeCookingTime;
  }

  public static RecommendRecipe of(RecipeEntity recipe, UserEntity author, LevelType difficulty, CookingTimeType cookingTime) {
    return new RecommendRecipe(
        recipe.getRcpId(),
        recipe.getRcpName(),
        author.getUsrNickname(),
        recipe.getRcpThumbnail(),
        recipe.getRating().stream().mapToDouble(RecipeRatingEntity::getRrRating).average().orElse(0),
        difficulty,
        cookingTime
    );
  }
}
