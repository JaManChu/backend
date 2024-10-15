package com.recipe.jamanchu.model.dto.response.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyRecipeInfo {

  private PageResponse<MyRecipes> myRecipes;
  private PageResponse<MyScrapedRecipes> myScrapedRecipes;
}