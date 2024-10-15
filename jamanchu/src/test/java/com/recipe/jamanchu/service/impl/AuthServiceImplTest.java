package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.exceptions.exception.CookieNotFoundException;
import com.recipe.jamanchu.exceptions.exception.RefreshTokenExpiredException;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.TokenType;
import com.recipe.jamanchu.model.type.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  private static final Long USERID = 1L;
  private static final String STR_ROLE = "USER";
  private static final String TOKEN_TYPE = "access";
  private static final String REFRESH_TOKEN = "refresh-token";
  private static final String NEW_ACCESS_TOKEN = "new-access-token";

  private Cookie[] cookies;

  @InjectMocks
  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    Cookie refreshCookie = new Cookie(TokenType.REFRESH.getValue(), REFRESH_TOKEN);
    cookies = new Cookie[] {refreshCookie};
  }

  @Test
  @DisplayName("checkEmail : 이미 사용 중인 이메일입니다.")
  void checkEmail_Email_Already_In_Use() {

    // given
    String email = "test@example.com";
    ResultResponse resultCode = ResultResponse.of(ResultCode.EMAIL_ALREADY_IN_USE);
    when(userAccessHandler.existsByEmail(email)).thenReturn(resultCode);

    // when
    ResultResponse result = authService.checkEmail(email);

    assertEquals(resultCode, result);

  }

  @Test
  @DisplayName("checkEmail : 사용할 수 있는 이메일입니다.")
  void checkEmail_Email_Available() {

    // given
    String email = "test@example.com";
    ResultResponse resultCode = ResultResponse.of(ResultCode.EMAIL_AVAILABLE);
    when(userAccessHandler.existsByEmail(email)).thenReturn(resultCode);

    // when
    ResultResponse result = authService.checkEmail(email);

    assertEquals(resultCode, result);
  }

  @Test
  @DisplayName("checkNickname : 이미 사용 중인 닉네임 입니다.")
  void checkNickname_Nickname_Already_In_Use() {

    // given
    String nickname = "nickname";
    ResultResponse resultCode = ResultResponse.of(ResultCode.NICKNAME_ALREADY_IN_USE);
    when(userAccessHandler.existsByNickname(nickname)).thenReturn(resultCode);

    // when
    ResultResponse result = authService.checkNickname(nickname);

    assertEquals(resultCode, result);

  }

  @Test
  @DisplayName("Nickname : 사용할 수 있는 닉네임 입니다.")
  void checkNickname_Nickname_Available() {

    // given
    String nickname = "nickname";
    ResultResponse resultCode = ResultResponse.of(ResultCode.NICKNAME_AVAILABLE);
    when(userAccessHandler.existsByNickname(nickname)).thenReturn(resultCode);

    // when
    ResultResponse result = authService.checkNickname(nickname);

    assertEquals(resultCode, result);
  }

  @Test
  @DisplayName("Access-Token 재발급 성공")
  void success_Reissue_RefreshToken() {
    // given
    when(request.getCookies()).thenReturn(cookies);
    when(jwtUtil.isExpired(REFRESH_TOKEN)).thenReturn(false);
    when(jwtUtil.getUserId(REFRESH_TOKEN)).thenReturn(USERID);
    when(jwtUtil.getRole(REFRESH_TOKEN)).thenReturn(STR_ROLE);
    when(jwtUtil.createJwt(TOKEN_TYPE, USERID, UserRole.valueOf(STR_ROLE))).thenReturn(NEW_ACCESS_TOKEN);

    // when
    ResultResponse resultResponse = authService.refreshToken(request, response);

    // then
    assertEquals(ResultCode.SUCCESS_REISSUE_REFRESH_TOKEN.getStatusCode(), resultResponse.getCode());
  }

  @Test
  @DisplayName("Access-Token 재발급 실패 : 쿠키값이 null인 경우")
  void reissue_RefreshToken_CookieIsNull() {
    // given
    when(request.getCookies()).thenReturn(null);

    // when & then
    assertThrows(CookieNotFoundException.class, () -> authService.refreshToken(request, response));

  }

  @Test
  @DisplayName("Access-Token 재발급 실패 : refresh 토큰이 만료가 된경우")
  void reissue_RefreshToken_Expired() {
    // given
    when(request.getCookies()).thenReturn(cookies);
    when(jwtUtil.isExpired(REFRESH_TOKEN)).thenReturn(true);

    // when & then
    assertThrows(RefreshTokenExpiredException.class, () -> authService.refreshToken(request, response));

  }
}