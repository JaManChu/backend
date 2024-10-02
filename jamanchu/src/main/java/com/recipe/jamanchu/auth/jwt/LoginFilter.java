package com.recipe.jamanchu.auth.jwt;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.dto.request.auth.UserDetailsDTO;
import com.recipe.jamanchu.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  public final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    String email = request.getParameter("email");
    String password = obtainPassword(request);

    logger.info("email = {}", request.getParameter("email"));
    logger.info("password = {}", obtainPassword(request));

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        email, password, null);

    return authenticationManager.authenticate(authToken);
  }


  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException {

    UserDetailsDTO userDetails = (UserDetailsDTO) authentication.getPrincipal();

    UserEntity user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(UserNotFoundException::new);

    String access = jwtUtil.createJwt("access", user.getUserId(), user.getRole());
    String refresh = jwtUtil.createJwt("refresh", user.getUserId(), user.getRole());

    response.addHeader("access-token", "Bearer " + access);
    response.addCookie(createCookie(refresh));
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write("로그인 성공");
  }

  private Cookie createCookie(String value) {
    Cookie cookie = new Cookie("refresh-token", value);
    cookie.setMaxAge(24 * 60 * 60);
    cookie.setHttpOnly(true);

    return cookie;
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) throws IOException {

    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    UserEntity user = userRepository.findByEmail(request.getParameter("email"))
        .orElseGet(() -> null);

    String enPassword = passwordEncoder.encode(request.getParameter("password"));

    if (user == null) {
      response.getWriter().write("이미 존재하는 아이디 입니다.");
    } else if (!enPassword.equals(user.getPassword())) {
      response.getWriter().write("비밀번호를 다시 입력해주세요");
    }
  }
}

