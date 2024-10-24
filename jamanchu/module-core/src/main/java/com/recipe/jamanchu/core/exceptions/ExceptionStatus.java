package com.recipe.jamanchu.core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionStatus {

  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 사용자를 찾을 수 없습니다."),
  SOCIAL_ACCOUNT(HttpStatus.BAD_REQUEST, "소셜 계정은 해당 소셜 계정에서 변경 가능합니다."),
  RECIPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 레시피를 찾을 수 없습니다."),
  UNMATCHED_USER(HttpStatus.BAD_REQUEST, "일치하는 사용자가 없습니다."),
  PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
  MISSING_EMAIL(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요!"),
  ACCESS_TOKEN_RETRIEVAL(HttpStatus.UNAUTHORIZED, "액세스 토큰을 가져오는 데 실패했습니다."),
  USER_INFO_RETRIEVAL(HttpStatus.UNAUTHORIZED, "사용자 정보를 가져오는 데 실패했습니다."),
  MISSING_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임을 입력해주세요!"),
  REFRESH_TOKEN_EXPIRED(HttpStatus.GONE, "다시 로그인을 해주세요!"),
  COOKIE_NOT_FOUND(HttpStatus.PRECONDITION_FAILED, "다시 로그인을 해주세요!"),
  WITHDREW_USER(HttpStatus.BAD_REQUEST, "이미 탈퇴한 회원입니다. 계정 복구를 희망하시면 "
      + "'user@example.com'으로 문의를 해주세요"),
  ;

  private final HttpStatus statusCode;
  private final String message;
}
