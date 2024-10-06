package com.recipe.jamanchu.auth.oauth2.handler;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.auth.oauth2.KakaoUserDetails;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.UserEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    KakaoUserDetails kakaoUserDetails = new KakaoUserDetails(oAuth2User.getAttributes());

    UserEntity user = userAccessHandler.findOrCreateUser(kakaoUserDetails);

    String access = jwtUtil.createJwt("access", user.getUserId(), user.getRole());
    String refresh = jwtUtil.createJwt("refresh", user.getUserId(), user.getRole());

    response.addHeader("access-token", "Bearer " + access);
    response.addCookie(createCookie(refresh));

    log.info("OAuth 로그인 성공");
    String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/api/v1/users/test")
        .queryParam("access", "Bearer " + access)
        .queryParam("refresh", refresh)
        .build()
        .toUriString();

    log.info("redirect -> http://localhost:8080/api/v1/users/test");
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  private Cookie createCookie(String value) {
    Cookie cookie = new Cookie("refresh-token", value);
    cookie.setMaxAge(24 * 60 * 60);
    cookie.setHttpOnly(true);

    return cookie;
  }
}

