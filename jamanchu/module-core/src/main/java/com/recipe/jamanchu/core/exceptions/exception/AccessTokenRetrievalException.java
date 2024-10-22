package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class AccessTokenRetrievalException extends GlobalException{

  public AccessTokenRetrievalException() {

    super(ExceptionStatus.ACCESS_TOKEN_RETRIEVAL);
  }

}