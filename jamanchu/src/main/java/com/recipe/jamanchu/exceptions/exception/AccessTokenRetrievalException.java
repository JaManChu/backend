package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class AccessTokenRetrievalException extends GlobalException{

  public AccessTokenRetrievalException() {

    super(ExceptionStatus.ACCESS_TOKEN_RETRIEVAL);
  }

}