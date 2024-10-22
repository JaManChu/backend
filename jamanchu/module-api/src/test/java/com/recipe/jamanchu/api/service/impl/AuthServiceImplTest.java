package com.recipe.jamanchu.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.api.auth.jwt.JwtUtil;
import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.core.exceptions.exception.CookieNotFoundException;
import com.recipe.jamanchu.core.exceptions.exception.RefreshTokenExpiredException;
import com.recipe.jamanchu.core.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.domain.model.dto.request.auth.PasswordCheckDTO;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.model.type.TokenType;
import com.recipe.jamanchu.domain.model.type.UserRole;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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

  @Mock
  private PasswordEncoder passwordEncoder;

  private static final Long USERID = 1L;
  private static final String STR_ROLE = "USER";
  private static final String TOKEN_TYPE = "access";
  private static final String REFRESH_TOKEN = "refresh-token";
  private static final String NEW_ACCESS_TOKEN = "new-access-token";
  private static final String ACCESS_TOKEN = "access-token";
  private static final String PASSWORD = "password";

  private Cookie[] cookies;
  private UserEntity user;
  private PasswordCheckDTO passwordCheckDTO;

  @InjectMocks
  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    Cookie refreshCookie = new Cookie(TokenType.REFRESH.getValue(), REFRESH_TOKEN);
    cookies = new Cookie[] {refreshCookie};

    user = UserEntity.builder()
        .userId(USERID)
        .password(PASSWORD)
        .build();

    passwordCheckDTO = new PasswordCheckDTO("password");
  }

  @Test
  @DisplayName("checkEmail : 이미 사용 중인 이메일입니다.")
  void checkEmail_Email_Already_In_Use() {

    // given
    String email = "test@example.com";
    ResultResponse response = ResultResponse.of(ResultCode.EMAIL_ALREADY_IN_USE, false);
    when(userAccessHandler.existsByEmail(email)).thenReturn(response);

    // when
    ResultResponse result = authService.checkEmail(email);

    assertEquals(response, result);

  }

  @Test
  @DisplayName("checkEmail : 사용할 수 있는 이메일입니다.")
  void checkEmail_Email_Available() {

    // given
    String email = "test@example.com";
    ResultResponse response = ResultResponse.of(ResultCode.EMAIL_AVAILABLE, true);
    when(userAccessHandler.existsByEmail(email)).thenReturn(response);

    // when
    ResultResponse result = authService.checkEmail(email);

    assertEquals(response, result);
  }

  @Test
  @DisplayName("checkNickname : 이미 사용 중인 닉네임 입니다.")
  void checkNickname_Nickname_Already_In_Use() {

    // given
    String nickname = "nickname";
    ResultResponse response = ResultResponse.of(ResultCode.NICKNAME_ALREADY_IN_USE, false);
    when(userAccessHandler.existsByNickname(nickname)).thenReturn(response);

    // when
    ResultResponse result = authService.checkNickname(nickname);

    assertEquals(response, result);

  }

  @Test
  @DisplayName("Nickname : 사용할 수 있는 닉네임 입니다.")
  void checkNickname_Nickname_Available() {

    // given
    String nickname = "nickname";
    ResultResponse resultCode = ResultResponse.of(ResultCode.NICKNAME_AVAILABLE, true);
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

  @Test
  @DisplayName("checkPassword : 비밀번호가 일치 합니다.")
  void checkPassword_Match() {

    // given
    when(request.getHeader(TokenType.ACCESS.getValue())).thenReturn(ACCESS_TOKEN);
    when(jwtUtil.getUserId(ACCESS_TOKEN)).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    ResultResponse response = ResultResponse.of(ResultCode.PASSWORD_MATCH, true);
    when(userAccessHandler.validateBeforePW(user.getPassword(), passwordCheckDTO.getPassword())).thenReturn(response);

    // when
    ResultResponse resultResponse = authService.checkPassword(passwordCheckDTO, request);

    // then
    assertEquals(response.getCode(), resultResponse.getCode());
  }

  @Test
  @DisplayName("checkPassword : 비밀번호가 일치하지 않습니다..")
  void checkPassword_MisMatch() {

    // given
    when(request.getHeader(TokenType.ACCESS.getValue())).thenReturn(ACCESS_TOKEN);
    when(jwtUtil.getUserId(ACCESS_TOKEN)).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    ResultResponse response = ResultResponse.of(ResultCode.PASSWORD_MISMATCH, false);
    when(userAccessHandler.validateBeforePW(user.getPassword(), passwordCheckDTO.getPassword())).thenReturn(response);

    // when
    ResultResponse resultResponse = authService.checkPassword(passwordCheckDTO, request);

    // then
    assertEquals(response.getCode(), resultResponse.getCode());
  }

  @Test
  @DisplayName("checkPassword : 존재하지 않는 회원")
  void checkPassword_NotFoundUser() {
    // given
    when(request.getHeader(TokenType.ACCESS.getValue())).thenReturn(ACCESS_TOKEN);
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> authService.checkPassword(passwordCheckDTO, request));
  }
}