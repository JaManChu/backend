package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class RefreshTokenExpiredException extends GlobalException {

  public RefreshTokenExpiredException() {

    super(ExceptionStatus.REFRESH_TOKEN_EXPIRED);
  }
}
