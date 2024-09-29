package com.recipe.jamanchu.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultCode {

  SUCCESS_SIGNUP(HttpStatus.CREATED, "회원가입 성공!");

  private final HttpStatus statusCode;
  private final String message;
}
