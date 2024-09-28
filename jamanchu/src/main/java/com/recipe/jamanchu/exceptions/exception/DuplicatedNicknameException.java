package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;

public class DuplicatedNicknameException extends GlobalException{

  public DuplicatedNicknameException() {

    super(ExceptionStatus.DUPLICATE_NICKNAME);
  }

}