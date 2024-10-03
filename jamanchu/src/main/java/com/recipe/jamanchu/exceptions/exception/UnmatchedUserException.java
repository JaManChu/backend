package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class UnmatchedUserException extends GlobalException{

  public UnmatchedUserException() {
    super(ExceptionStatus.UNMATCHED_USER);
  }
}
