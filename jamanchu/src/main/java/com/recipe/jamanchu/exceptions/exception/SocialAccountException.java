package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class SocialAccountException extends GlobalException{

  public SocialAccountException() {

    super(ExceptionStatus.SOCIAL_ACCOUNT);
  }
}