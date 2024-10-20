package com.recipe.jamanchu.model.dto.response.recipes;

import java.util.List;
import lombok.Getter;

@Getter
public class RecommendRecipes {

    private final List<RecommendRecipe> recipes;

    private RecommendRecipes() {
      this.recipes = null;
    }

    private RecommendRecipes(List<RecommendRecipe> recipes) {
      this.recipes = recipes;
    }

    public static RecommendRecipes of(List<RecommendRecipe> recipes) {
      return new RecommendRecipes(recipes);
    }

    public static RecommendRecipes empty(){
      return new RecommendRecipes();
    }

}
