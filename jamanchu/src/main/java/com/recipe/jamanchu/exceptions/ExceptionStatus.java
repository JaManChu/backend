package com.recipe.jamanchu.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionStatus {

  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 사용자를 찾을 수 없습니다."),
  DUPLICATE_EMAIL(HttpStatus.UNAUTHORIZED, "이미 존재하는 이메일 입니다."),
  DUPLICATE_NICKNAME(HttpStatus.UNAUTHORIZED, "이미 존재하는 닉네임 입니다.");

  private final HttpStatus statusCode;
  private final String message;
}
