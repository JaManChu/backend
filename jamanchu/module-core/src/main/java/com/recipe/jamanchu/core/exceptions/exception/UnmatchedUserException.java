package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class UnmatchedUserException extends GlobalException{

  public UnmatchedUserException() {
    super(ExceptionStatus.UNMATCHED_USER);
  }
}
