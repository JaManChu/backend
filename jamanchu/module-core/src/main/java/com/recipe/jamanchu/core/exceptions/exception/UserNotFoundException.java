package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class UserNotFoundException extends GlobalException {

  public UserNotFoundException() {

    super(ExceptionStatus.USER_NOT_FOUND);
  }
}