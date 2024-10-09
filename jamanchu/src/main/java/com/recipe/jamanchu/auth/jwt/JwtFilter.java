package com.recipe.jamanchu.auth.jwt;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.model.dto.request.auth.UserDetailsDTO;
import com.recipe.jamanchu.model.type.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    logger.info("requestURI -> {}", requestURI);

    // 인증이 필요 없는 경로
    if (isExcludedPath(requestURI)) {
      filterChain.doFilter(request, response);
      return;
    }

    String authorizationHeader = request.getHeader("access-token");

    // Bearer 토큰이 헤더에 있는지 확인
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      logger.info("Access-token is null or doesn't start with Bearer");
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = authorizationHeader.substring(7);

    try {
      // access 토큰이 만료가 되지 않은 경우
      jwtUtil.isExpired(accessToken);

      setAuthenticationFromToken(response, accessToken);
    } catch (ExpiredJwtException e) {
      expiredAccessToken(request, response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void setAuthenticationFromToken(HttpServletResponse response, String token) {
    response.addHeader("access-token", "Bearer " + token);

    Authentication authToken = getAuthToken(token);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }


  // access 토큰이 만료되었을 경우 refresh 토큰 검증 후 재 발급
  private void expiredAccessToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.info("Access-Token is Expired!!");
    String refreshToken = getRefreshTokenFromCookies(request);

    if (refreshToken == null) {
      setResponse(response);
      return;
    }

    try {
      jwtUtil.isExpired(refreshToken);
      String newAccessToken = createNewAccessToken(refreshToken);

      setAuthenticationFromToken(response, newAccessToken);
    } catch (ExpiredJwtException e) {
      setResponse(response);
    }
  }

  // refresh 토큰 반환
  private String getRefreshTokenFromCookies(HttpServletRequest request) {
    return Arrays.stream(request.getCookies())
        .filter(cookie -> "refresh-token".equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }

  private Authentication getAuthToken(String token) {
    Long userId = jwtUtil.getUserId(token);
    UserRole role = UserRole.valueOf(jwtUtil.getRole(token));

    UserEntity user = UserEntity.builder()
        .userId(userId)
        .role(role)
        .build();

    UserDetailsDTO userDetails = new UserDetailsDTO(user);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  // access 토큰 재발급
  private String createNewAccessToken(String refreshToken) {

    logger.info("Create New Access-Token!");

    Long userId = jwtUtil.getUserId(refreshToken);
    UserRole role = UserRole.valueOf(jwtUtil.getRole(refreshToken));

    return jwtUtil.createJwt("access", userId, role);
  }

  // 오류 메세지 전송
  private void setResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write("인증 토큰이 만료되었습니다. 다시 로그인해 주세요.");
  }

  private boolean isExcludedPath(String requestURI) {
    return requestURI.equals("/")
        || requestURI.equals("/api/v1/users/login")
        || requestURI.equals("/api/v1/users/signup")
        || requestURI.equals("/api/v1/users/test")
        || Pattern.matches("/api/v1/notify/.*",requestURI);
  }
}


