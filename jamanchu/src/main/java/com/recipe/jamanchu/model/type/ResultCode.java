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
  ;

  private final HttpStatus statusCode;
  private final String message;
}
