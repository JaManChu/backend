package com.recipe.jamanchu.service.impl;

import static com.recipe.jamanchu.model.type.ResultCode.SUCCESS_GET_USER_INFO;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.auth.oauth2.CustomOauth2UserService;
import com.recipe.jamanchu.auth.oauth2.KakaoUserDetails;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.model.dto.request.auth.DeleteUserDTO;
import com.recipe.jamanchu.model.dto.request.auth.LoginDTO;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.auth.UserInfoDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.TokenType;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final BCryptPasswordEncoder passwordEncoder;
  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;
  private final CustomOauth2UserService oauth2UserService;
  private final String REDIRECT_URI = "https://frontend-dun-eight-78.vercel.app/users/login/auth/kakao";

  // 회원가입
  @Override
  public ResultCode signup(SignupDTO signupDTO) {

    // 회원 정보 저장
    userAccessHandler.saveUser(UserEntity.builder()
        .email(signupDTO.getEmail())
        .password(passwordEncoder.encode(signupDTO.getPassword()))
        .nickname(signupDTO.getNickname())
        .role(UserRole.USER)
        .build());

    return ResultCode.SUCCESS_SIGNUP;
  }

  // 일반 로그인
  @Override
  public ResultResponse login(LoginDTO loginDTO, HttpServletResponse response) {

    UserEntity user = userAccessHandler.findByEmail(loginDTO.getEmail());

    userAccessHandler.validatePassword(user.getPassword(), loginDTO.getPassword());

    String access = jwtUtil.createJwt("access", user.getUserId(), user.getRole());
    String refresh = jwtUtil.createJwt("refresh", user.getUserId(), user.getRole());

    response.addHeader(TokenType.ACCESS.getValue(), "Bearer " + access);
    response.addHeader(HttpHeaders.SET_COOKIE, createCookie(refresh).toString());

    return new ResultResponse(ResultCode.SUCCESS_LOGIN, user.getNickname());
  }

  // 카카오 로그인
  @Override
  public String kakaoLogin(String code, HttpServletResponse response) {

    // "인가 코드"로 "액세스 토큰" 요청
    String accessToken = oauth2UserService.getAccessToken(code);

    // 토큰으로 사용자 정보 요청
    KakaoUserDetails userInfo = oauth2UserService.getUserDetails(accessToken);

    // 카카오ID로 회원가입 OR 로그인 처리
    UserEntity user = userAccessHandler.findOrCreateUser(userInfo);

    String access = jwtUtil.createJwt("access", user.getUserId(), user.getRole());
    String refresh = jwtUtil.createJwt("refresh", user.getUserId(), user.getRole());

    response.addHeader(HttpHeaders.SET_COOKIE, createCookie(refresh).toString());

    return UriComponentsBuilder.fromUriString(REDIRECT_URI)
        .queryParam(TokenType.ACCESS.getValue(), access)
        .queryParam("nickname", user.getNickname())
        .build()
        .toUriString();
  }

  // 회원 정보 수정
  @Override
  public ResultCode updateUserInfo(HttpServletRequest request, UserUpdateDTO userUpdateDTO) {

    UserEntity user = userAccessHandler
        .findByUserId(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue())));

    // 비밀번호 중복 체크
    userAccessHandler.validatePassword(user.getPassword(), userUpdateDTO.getBeforePassword());

    // 소셜 계정 체크
    userAccessHandler.isSocialUser(user.getProvider());

    // 회원 정보 저장
    userAccessHandler.saveUser(UserEntity.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .password(passwordEncoder.encode(userUpdateDTO.getAfterPassword()))
        .nickname(userUpdateDTO.getNickname())
        .role(user.getRole())
        .build());

    return ResultCode.SUCCESS_UPDATE_USER_INFO;
  }

  // 회원 탈퇴
  @Override
  public ResultCode deleteUser(HttpServletRequest request, DeleteUserDTO deleteUserDTO) {

    UserEntity user = userAccessHandler
        .findByUserId(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue())));

    if (user.getProvider() == null) {
      userAccessHandler.validatePassword(user.getPassword(), deleteUserDTO.getPassword());
    }

    userAccessHandler.deleteUser(user);
    return ResultCode.SUCCESS_DELETE_USER;
  }

  // 회원 정보 조회
  @Override
  public ResultResponse getUserInfo(HttpServletRequest request) {

    UserEntity user = userAccessHandler
        .findByUserId(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue())));

    return new ResultResponse(SUCCESS_GET_USER_INFO,
        new UserInfoDTO(user.getEmail(), user.getNickname()));
  }


  private ResponseCookie createCookie(String value) {
    return ResponseCookie.from(TokenType.REFRESH.getValue(), value)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(24 * 60 * 60)
        .sameSite("None")
        .build();
  }

}

