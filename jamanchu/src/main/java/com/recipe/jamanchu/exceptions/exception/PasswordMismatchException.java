package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class PasswordMismatchException extends GlobalException{

  public PasswordMismatchException() {

    super(ExceptionStatus.PASSWORD_MISMATCH);
  }
}