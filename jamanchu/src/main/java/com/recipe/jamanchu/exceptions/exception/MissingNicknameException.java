package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class MissingNicknameException extends GlobalException{

  public MissingNicknameException() {

    super(ExceptionStatus.MISSING_NICKNAME);
  }
}
