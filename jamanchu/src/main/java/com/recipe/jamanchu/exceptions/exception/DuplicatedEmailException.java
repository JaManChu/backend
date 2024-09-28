package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class DuplicatedEmailException extends GlobalException{

  public DuplicatedEmailException() {

    super(ExceptionStatus.DUPLICATE_EMAIL);
  }
}