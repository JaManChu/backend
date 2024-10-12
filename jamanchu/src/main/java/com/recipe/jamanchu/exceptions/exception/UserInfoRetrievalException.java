package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class UserInfoRetrievalException extends GlobalException{

  public UserInfoRetrievalException() {

    super(ExceptionStatus.USER_INFO_RETRIEVAL);
  }

}