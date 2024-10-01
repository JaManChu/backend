package com.recipe.jamanchu.auth.oauth2.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

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

  @InjectMocks
  private OAuth2SuccessHandler oAuth2SuccessHandler;

  @Test
  @DisplayName("OAuth2 로그인 성공: 새로운 사용자")
  void oAuthLoginNewUser() throws IOException {
    // given
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", "test@example.com");

    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", "testNickname");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", "providerId");
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", properties);

    when(authentication.getPrincipal()).thenReturn(oAuth2User);
    when(oAuth2User.getAttributes()).thenReturn(attributes);

    String providerId = "providerId";
    String email = "test@example.com";
    String nickname = "testNickname";

    when(userRepository.findByProviderId(providerId)).thenReturn(Optional.empty());

    String encodedPassword = "encodedPassword";
    when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

    UserEntity user = UserEntity.builder()
        .userId(1L)
        .email(email)
        .password(encodedPassword)
        .nickname(nickname)
        .provider("kakao")
        .providerId(providerId)
        .role(UserRole.USER)
        .build();
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);

    String accessToken = "accessToken";
    String refreshToken = "refreshToken";
    when(jwtUtil.createJwt("access", user.getUserId(), user.getRole())).thenReturn(accessToken);
    when(jwtUtil.createJwt("refresh", user.getUserId(), user.getRole())).thenReturn(refreshToken);

    PrintWriter writer = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    // when
    oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

    // then
    verify(response).addHeader("access-token", accessToken);
    verify(response).addCookie(any(Cookie.class));
    verify(response).setStatus(HttpServletResponse.SC_OK);
    verify(response).setContentType("application/json");
    verify(response).setCharacterEncoding("UTF-8");
    verify(writer).write("로그인 성공");
  }

  @Test
  @DisplayName("OAuth2 로그인 성공: 기존 사용자")
  void oAuthLoginExistUser() throws IOException {
    // given
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", "test@example.com");

    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", "testNickname");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", "providerId");
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", properties);

    when(authentication.getPrincipal()).thenReturn(oAuth2User);
    when(oAuth2User.getAttributes()).thenReturn(attributes);

    String providerId = "providerId";
    String email = "test@example.com";
    String nickname = "testNickname";

    UserEntity user = UserEntity.builder()
        .userId(1L)
        .email(email)
        .password("existingPassword")
        .nickname(nickname)
        .provider("kakao")
        .providerId(providerId)
        .role(UserRole.USER)
        .build();

    when(userRepository.findByProviderId(providerId)).thenReturn(Optional.of(user));

    String accessToken = "accessToken";
    String refreshToken = "refreshToken";
    when(jwtUtil.createJwt("access", user.getUserId(), user.getRole())).thenReturn(accessToken);
    when(jwtUtil.createJwt("refresh", user.getUserId(), user.getRole())).thenReturn(refreshToken);

    PrintWriter writer = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    // when
    oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

    // then
    verify(response).addHeader("access-token", accessToken);
    verify(response).addCookie(any(Cookie.class));
    verify(response).setStatus(HttpServletResponse.SC_OK);
    verify(response).setContentType("application/json");
    verify(response).setCharacterEncoding("UTF-8");
    verify(writer).write("로그인 성공");
  }
}