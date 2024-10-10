package com.recipe.jamanchu.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultCode {

  SUCCESS_SIGNUP(HttpStatus.CREATED, "회원가입 성공!"),
  SUCCESS_RETRIEVE_COMMENTS(HttpStatus.OK, "댓글 조회 성공!"),
  SUCCESS_COMMENTS(HttpStatus.CREATED, "댓글 작성 성공!"),
  SUCCESS_UPDATE_COMMENTS(HttpStatus.OK, "댓글 수정 성공!"),
  SUCCESS_DELETE_COMMENT(HttpStatus.OK, "댓글 삭제 성공!"),
  SUCCESS_CR_RECIPE(HttpStatus.CREATED, "스크래핑 레시피 저장 성공!"),
  SUCCESS_UPDATE_USER_INFO(HttpStatus.OK, "회원 정보 수정 성공!"),
  SUCCESS_INSERT_CR_DATA(HttpStatus.CREATED, "데이터 분산 저장 성공!"),
  SUCCESS_DELETE_USER(HttpStatus.OK, "회원 탈퇴 성공!"),
  SUCCESS_GET_USER_INFO(HttpStatus.OK, "회원 정보 조회 성공!"),
  SUCCESS_LOGIN(HttpStatus.OK, "로그인 성공"),
  EMAIL_AVAILABLE(HttpStatus.OK, "사용할 수 있는 이메일입니다."),
  EMAIL_ALREADY_IN_USE(HttpStatus.OK, "이미 사용 중인 이메일입니다."),
  ;

  private final HttpStatus statusCode;
  private final String message;
}
