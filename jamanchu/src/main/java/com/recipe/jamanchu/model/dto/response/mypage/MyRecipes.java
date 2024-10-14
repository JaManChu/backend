package com.recipe.jamanchu.model.dto.response.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyRecipes {
  private Long myRecipeId;
  private String myRecipeName;
  private String myRecipeThumbnail;
}