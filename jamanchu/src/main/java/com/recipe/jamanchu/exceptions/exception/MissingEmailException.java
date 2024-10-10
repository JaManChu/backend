package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class MissingEmailException extends GlobalException{

  public MissingEmailException() {

    super(ExceptionStatus.MISSING_EMAIL);
  }
}
