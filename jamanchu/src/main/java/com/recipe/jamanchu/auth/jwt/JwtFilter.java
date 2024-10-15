package com.recipe.jamanchu.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.jamanchu.model.type.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Options 요청 및 인증이 필요없는 경로 토큰 검증 제외
    if (HttpMethod.OPTIONS.matches(request.getMethod())
        || isExcludedPath(request.getRequestURI())) {

      filterChain.doFilter(request, response);
      return;
    }

    // Header에서 Access-Token 추출
    String tokenHeader = request.getHeader(TokenType.ACCESS.getValue());

    // Bearer 시작 여부 및 null 값 검증
    if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
      log.info("Invalid or missing access-token");
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = tokenHeader.substring(7);

    // Access-Token 만료시간 검증
    if (jwtUtil.isExpired(accessToken)) {
      log.info("access-token is expired");
      setResponse(response);
      return;
    }

    setAuthentication(accessToken);
    filterChain.doFilter(request, response);
  }

  // userId를 추출 및 인증 정보를 설정
  private void setAuthentication(String token) {
    Long userId = jwtUtil.getUserId(token);

    Authentication authentication = jwtUtil.getAuthentication(userId.toString());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  // Access-Token 만료시 401 반환
  private void setResponse(HttpServletResponse response) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    Map<String, String> body = Map.of("message", "access-token 만료");
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }

  // Jwt 검증 제외 경로
  private boolean isExcludedPath(String requestURI) {
    return requestURI.equals("/")
        || requestURI.equals("/api/v1/users/login")
        || requestURI.equals("/api/v1/users/signup")
        || requestURI.equals("/api/v1/auth/email-check")
        || requestURI.equals("/api/v1/auth/nickname-check")
        || requestURI.equals("/api/v1/users/login/auth/kakao")
        || requestURI.equals("/api/v1/auth/token/refresh");
  }
}