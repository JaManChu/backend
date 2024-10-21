package com.recipe.jamanchu.domain.model.dto.response.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyScrapedRecipes {
  private Long recipeId;
  private String recipeName;
  private String recipeAuthor;
  private String recipeThumbnail;
}