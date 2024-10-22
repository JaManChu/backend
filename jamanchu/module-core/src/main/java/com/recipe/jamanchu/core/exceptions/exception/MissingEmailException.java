package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class MissingEmailException extends GlobalException{

  public MissingEmailException() {

    super(ExceptionStatus.MISSING_EMAIL);
  }
}
