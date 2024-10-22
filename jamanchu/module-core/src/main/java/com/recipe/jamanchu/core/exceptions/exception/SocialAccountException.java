package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class SocialAccountException extends GlobalException {

  public SocialAccountException() {

    super(ExceptionStatus.SOCIAL_ACCOUNT);
  }
}