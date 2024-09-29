package com.recipe.jamanchu.auth.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.model.dto.request.UserDetailsDTO;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginFilterTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private LoginFilter loginFilter;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain chain;

  @Mock
  private Authentication authentication;

  @Mock
  private AuthenticationException authException;


  @Test
  @DisplayName("로그인 성공")
  void login() throws Exception {
    // given
    String email = "test@example.com";
    String password = "password";
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    UserEntity user = UserEntity.builder()
        .userId(1L)
        .email(email)
        .password(passwordEncoder.encode(password))
        .role(UserRole.USER)
        .build();

    UserDetailsDTO userDetails = new UserDetailsDTO(user);

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(jwtUtil.createJwt("access", user.getUserId(), user.getRole())).thenReturn("accessToken");
    when(jwtUtil.createJwt("refresh", user.getUserId(), user.getRole())).thenReturn(refreshToken);

    PrintWriter writer = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    // when
    loginFilter.successfulAuthentication(request, response, chain, authentication);

    // then
    verify(response).addHeader("access", accessToken);
    verify(response).addCookie(any(Cookie.class));
    verify(response).setStatus(HttpServletResponse.SC_OK);
    verify(response).setContentType("application/json");
    verify(response).setCharacterEncoding("UTF-8");
    verify(writer).write("로그인 성공");
  }

  @Test
  @DisplayName("로그인 실패: 사용자 없음")
  void loginUserNotFound() throws IOException {
    // given
    String email = "test@example.com";
    String password = "password";

    when(request.getParameter("email")).thenReturn(email);
    when(request.getParameter("password")).thenReturn(password);
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    PrintWriter writer = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    // when
    loginFilter.unsuccessfulAuthentication(request, response, authException);

    // then
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(response).setContentType("application/json");
    verify(response).setCharacterEncoding("UTF-8");
    verify(writer).write("이미 존재하는 아이디 입니다.");
  }

  @Test
  @DisplayName("로그인 실패: 비밀번호 불일치")
  void loginValidatePassword() throws IOException {
    // given
    String email = "test@example.com";
    String password = "1234";
    String encodedPassword = "encodedWrongPassword";

    UserEntity user = UserEntity.builder()
        .userId(1L)
        .email(email)
        .password(passwordEncoder.encode(password))
        .role(UserRole.USER)
        .build();

    when(request.getParameter("email")).thenReturn(email);
    when(request.getParameter("password")).thenReturn(password);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

    PrintWriter writer = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    // when
    loginFilter.unsuccessfulAuthentication(request, response, authException);

    // then
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(response).setContentType("application/json");
    verify(response).setCharacterEncoding("UTF-8");
    verify(writer).write("비밀번호를 다시 입력해주세요");
  }
}

