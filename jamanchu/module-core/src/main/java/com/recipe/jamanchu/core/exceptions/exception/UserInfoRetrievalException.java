package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class UserInfoRetrievalException extends GlobalException {

  public UserInfoRetrievalException() {

    super(ExceptionStatus.USER_INFO_RETRIEVAL);
  }

}