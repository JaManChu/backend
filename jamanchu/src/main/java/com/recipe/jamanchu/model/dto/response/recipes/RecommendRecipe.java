package com.recipe.jamanchu.model.dto.response.recipes;

import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import lombok.Getter;

@Getter
public class RecommendRecipe {

  private final Long recipeId;

  private final String recipeName;

  private final String recipeAuthor;

  private final String recipeThumbnail;

  private RecommendRecipe(Long recipeId, String recipeName, String recipeAuthor, String recipeThumbnail) {
    this.recipeId = recipeId;
    this.recipeName = recipeName;
    this.recipeAuthor = recipeAuthor;
    this.recipeThumbnail = recipeThumbnail;
  }

  public static RecommendRecipe of(RecipeEntity recipe, UserEntity author) {
    return new RecommendRecipe(recipe.getId(), recipe.getName(), author.getNickname(), recipe.getThumbnail());
  }
}
