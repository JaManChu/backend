package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class PasswordMismatchException extends GlobalException {

  public PasswordMismatchException() {

    super(ExceptionStatus.PASSWORD_MISMATCH);
  }
}