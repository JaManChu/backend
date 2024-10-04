package com.recipe.jamanchu.auth.oauth2.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.auth.oauth2.KakaoUserDetails;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.model.type.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.RedirectStrategy;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private Authentication authentication;

  @Mock
  private OAuth2User oAuth2User;

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private RedirectStrategy redirectStrategy;

  @InjectMocks
  private OAuth2SuccessHandler oAuth2SuccessHandler;

  private static final String PROVIDER_ID = "providerId";
  private static final String EMAIL = "test@example.com";
  private static final String NICKNAME = "testNickname";
  private static final String PASSWORD = "password";
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String REFRESH_TOKEN = "refreshToken";

  private UserEntity user;
  private Map<String, Object> attributes;

  @BeforeEach
  void setUp() {
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", EMAIL);

    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", NICKNAME);

    attributes = new HashMap<>();
    attributes.put("id", PROVIDER_ID);
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", properties);

    user = UserEntity.builder()
        .userId(1L)
        .email(EMAIL)
        .nickname(NICKNAME)
        .password(PASSWORD)
        .provider("kakao")
        .providerId(PROVIDER_ID)
        .role(UserRole.USER)
        .build();
  }

  @Test
  @DisplayName("OAuth2 로그인 성공: 기존 사용자")
  void oAuthLoginExistUser() throws IOException {
    // given

    when(authentication.getPrincipal()).thenReturn(oAuth2User);
    when(oAuth2User.getAttributes()).thenReturn(attributes);

    when(userAccessHandler.findOrCreateUser(any(KakaoUserDetails.class))).thenReturn(user);

    when(jwtUtil.createJwt("access", user.getUserId(), user.getRole())).thenReturn(ACCESS_TOKEN);
    when(jwtUtil.createJwt("refresh", user.getUserId(), user.getRole())).thenReturn(REFRESH_TOKEN);

    // RedirectStrategy 설정
    oAuth2SuccessHandler.setRedirectStrategy(redirectStrategy);

    // when
    oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

    // then
    verify(response).addHeader("access-token", "Bearer " + ACCESS_TOKEN);
    verify(response).addCookie(any(Cookie.class));
    verify(redirectStrategy).sendRedirect(eq(request), eq(response), anyString());
  }
}