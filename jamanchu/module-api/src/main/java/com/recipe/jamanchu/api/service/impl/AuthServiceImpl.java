package com.recipe.jamanchu.api.service.impl;

import com.recipe.jamanchu.api.auth.jwt.JwtUtil;
import com.recipe.jamanchu.api.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.core.exceptions.exception.CookieNotFoundException;
import com.recipe.jamanchu.core.exceptions.exception.RefreshTokenExpiredException;
import com.recipe.jamanchu.domain.model.dto.request.auth.PasswordCheckDTO;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.model.type.TokenType;
import com.recipe.jamanchu.domain.model.type.UserRole;
import com.recipe.jamanchu.api.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;

  @Override
  public ResultResponse checkEmail(String email) {

    return userAccessHandler.existsByEmail(email);
  }

  @Override
  public ResultResponse checkNickname(String nickname) {

    return userAccessHandler.existsByNickname(nickname);
  }

  @Override
  public ResultResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();

    // 쿠키 검증
    if (cookies == null || cookies.length == 0) {
      log.info("No cookies found");
      throw new CookieNotFoundException();
    }

    log.info("cookies.length -> {}", cookies.length);

    // refresh-token 추출
    String refreshToken =  Arrays.stream(request.getCookies())
        .filter(cookie -> TokenType.REFRESH.getValue().equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);

    // refresh-token 검증
    if (refreshToken == null || jwtUtil.isExpired(refreshToken)) {
      throw new RefreshTokenExpiredException();
    }

    Long userId = jwtUtil.getUserId(refreshToken);
    UserRole role = UserRole.valueOf(jwtUtil.getRole(refreshToken));
    String access = jwtUtil.createJwt("access", userId, role);

    log.info("access-token 재발급 성공!!!");
    response.addHeader(TokenType.ACCESS.getValue(), "Bearer " + access);

    return ResultResponse.of(ResultCode.SUCCESS_REISSUE_REFRESH_TOKEN);
  }

  @Override
  public ResultResponse checkPassword(PasswordCheckDTO passwordCheckDTO,
      HttpServletRequest request) {

    UserEntity user = userAccessHandler
        .findByUserId(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue())));

    return userAccessHandler.validateBeforePW(user.getPassword(), passwordCheckDTO.getPassword());
  }
}
