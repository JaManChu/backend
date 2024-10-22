package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class RefreshTokenExpiredException extends GlobalException {

  public RefreshTokenExpiredException() {

    super(ExceptionStatus.REFRESH_TOKEN_EXPIRED);
  }
}
