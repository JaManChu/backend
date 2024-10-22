package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;

public class MissingNicknameException extends GlobalException{

  public MissingNicknameException() {

    super(ExceptionStatus.MISSING_NICKNAME);
  }
}
