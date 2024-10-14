package com.recipe.jamanchu.model.dto.response.mypage;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyRecipeInfo {

  private List<MyRecipes> myRecipes;
  private List<MyScrapedRecipes> myScrapedRecipes;
}