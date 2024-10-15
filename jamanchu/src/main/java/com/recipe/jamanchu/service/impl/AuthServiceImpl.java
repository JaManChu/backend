package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.exceptions.exception.RefreshTokenExpiredException;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.TokenType;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.service.AuthService;
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
    log.info("cookies.length -> {}", cookies.length);

    String refreshToken =  Arrays.stream(request.getCookies())
        .filter(cookie -> TokenType.REFRESH.getValue().equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);

    if (refreshToken == null || jwtUtil.isExpired(refreshToken)) {
      throw new RefreshTokenExpiredException();
    }

    Long userId = jwtUtil.getUserId(refreshToken);
    UserRole role = UserRole.valueOf(jwtUtil.getRole(refreshToken));

    String access = jwtUtil.createJwt("access", userId, role);

    response.addHeader(TokenType.ACCESS.getValue(), "Bearer " + access);

    return ResultResponse.of(ResultCode.SUCCESS_REISSUE_REFRESH_TOKEN);
  }
}
