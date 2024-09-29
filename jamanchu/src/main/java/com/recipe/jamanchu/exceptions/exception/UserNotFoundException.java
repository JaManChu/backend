package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class UserNotFoundException extends GlobalException{

  public UserNotFoundException() {

    super(ExceptionStatus.USER_NOT_FOUND);
  }
}