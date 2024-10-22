package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class CookieNotFoundException extends GlobalException {

  public CookieNotFoundException() {
    super(ExceptionStatus.COOKIE_NOT_FOUND);
  }
}
