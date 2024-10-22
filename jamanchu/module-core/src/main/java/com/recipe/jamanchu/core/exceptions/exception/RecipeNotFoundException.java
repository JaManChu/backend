package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class RecipeNotFoundException extends GlobalException {

  public RecipeNotFoundException() {
    super(ExceptionStatus.RECIPE_NOT_FOUND);
  }
}
