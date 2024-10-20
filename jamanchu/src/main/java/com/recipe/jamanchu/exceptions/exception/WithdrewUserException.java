package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class WithdrewUserException extends GlobalException {

  public WithdrewUserException() {
    super(ExceptionStatus.WITHDREW_USER);
  }
}