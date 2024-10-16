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
  SUCCESS_REGISTER_RECIPE(HttpStatus.CREATED, "레시피 등록 성공!"),
  SUCCESS_UPDATE_RECIPE(HttpStatus.OK, "레시피 수정 성공!"),
  SUCCESS_DELETE_RECIPE(HttpStatus.OK, "레시피를 정상적으로 삭제하였습니다."),
  SUCCESS_RETRIEVE_ALL_RECIPES(HttpStatus.OK, "전체 레시피 조회 성공!"),
  SUCCESS_RETRIEVE_RECIPES(HttpStatus.OK, "레시피 조회 성공!"),
  SUCCESS_RETRIEVE_RECIPES_DETAILS(HttpStatus.OK, "레시피 상세 조회 성공"),
  SUCCESS_RETRIEVE_ALL_RECIPES_BY_RATING(HttpStatus.OK, "인기 레시피 조회 성공"),
  SUCCESS_SCRAPED_RECIPE(HttpStatus.OK, "레시피 찜하기 성공"),
  SUCCESS_CANCELED_SCRAP_RECIPE(HttpStatus.OK, "레시피 찜하기 취소 성공"),
  SUCCESS_LOGIN(HttpStatus.OK, "로그인 성공"),
  EMAIL_AVAILABLE(HttpStatus.OK, "사용할 수 있는 이메일 입니다."),
  EMAIL_ALREADY_IN_USE(HttpStatus.OK, "이미 사용 중인 이메일 입니다."),
  NICKNAME_AVAILABLE(HttpStatus.OK, "사용할 수 있는 닉네임 입니다."),
  NICKNAME_ALREADY_IN_USE(HttpStatus.OK, "이미 사용 중인 닉네임 입니다."),
  SUCCESS_GET_USER_RECIPES_INFO(HttpStatus.OK, "회원 레시피 정보 조회 성공"),
  SUCCESS_REISSUE_REFRESH_TOKEN(HttpStatus.OK, "access-token 재발급 성공"),
  PASSWORD_MATCH(HttpStatus.OK, "비밀번호가 일치 합니다."),
  PASSWORD_MISMATCH(HttpStatus.OK, "비밀번호가 일치하지 않습니다."),
  ;

  private final HttpStatus statusCode;
  private final String message;
}
