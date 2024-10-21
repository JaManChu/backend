package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class WithdrewUserException extends GlobalException {

  public WithdrewUserException() {
    super(ExceptionStatus.WITHDREW_USER);
  }
}