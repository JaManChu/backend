package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class RecipeNotFoundException extends GlobalException{

  public RecipeNotFoundException() {
    super(ExceptionStatus.RECIPE_NOT_FOUND);
  }
}
