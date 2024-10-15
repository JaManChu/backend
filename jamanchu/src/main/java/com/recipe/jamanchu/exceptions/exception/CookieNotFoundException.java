package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class CookieNotFoundException extends GlobalException {

  public CookieNotFoundException() {
    super(ExceptionStatus.COOKIE_NOT_FOUND);
  }
}
