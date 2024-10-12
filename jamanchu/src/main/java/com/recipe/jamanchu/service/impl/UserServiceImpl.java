package com.recipe.jamanchu.service.impl;

import static com.recipe.jamanchu.model.type.ResultCode.SUCCESS_GET_USER_INFO;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.model.dto.request.auth.DeleteUserDTO;
import com.recipe.jamanchu.model.dto.request.auth.LoginDTO;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.auth.UserInfoDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final BCryptPasswordEncoder passwordEncoder;
  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;

  @Override
  public ResultCode signup(SignupDTO signupDTO) {

    // 닉네임 중복 체크
    userAccessHandler.existsByNickname(signupDTO.getNickname());

    // 회원 정보 저장
    userAccessHandler.saveUser(UserEntity.builder()
        .email(signupDTO.getEmail())
        .password(passwordEncoder.encode(signupDTO.getPassword()))
        .nickname(signupDTO.getNickname())
        .role(UserRole.USER)
        .build());

    return ResultCode.SUCCESS_SIGNUP;
  }

  @Override
  public ResultCode login(LoginDTO loginDTO, HttpServletResponse response) {
    UserEntity user = userAccessHandler.findByEmail(loginDTO.getEmail());
    userAccessHandler.validatePassword(user.getPassword(), loginDTO.getPassword());

    String access = jwtUtil.createJwt("access", user.getUserId(), user.getRole());
    String refresh = jwtUtil.createJwt("refresh", user.getUserId(), user.getRole());

    response.addHeader("Access-Token", "Bearer " + access);
    response.addCookie(createCookie(refresh));

    return ResultCode.SUCCESS_LOGIN;
  }


  @Override
  public ResultCode updateUserInfo(HttpServletRequest request, UserUpdateDTO userUpdateDTO) {
    UserEntity user = userAccessHandler
        .findByUserId(jwtUtil.getUserId(request.getHeader("Access-Token")));

    // 비밀번호 중복 체크
    userAccessHandler.validatePassword(user.getPassword(), userUpdateDTO.getBeforePassword());

    // 소셜 계정 체크
    userAccessHandler.isSocialUser(user.getProvider());
    // 닉네임 중복 체크
    if (!user.getNickname().equals(userUpdateDTO.getNickname())) {
      userAccessHandler.existsByNickname(userUpdateDTO.getNickname());
    }
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

  @Override
  public ResultCode deleteUser(HttpServletRequest request, DeleteUserDTO deleteUserDTO) {
    UserEntity user = userAccessHandler
        .findByUserId(jwtUtil.getUserId(request.getHeader("Access-Token")));

    if (user.getProvider() == null) {
      userAccessHandler.validatePassword(user.getPassword(), deleteUserDTO.getPassword());
    }

    userAccessHandler.deleteUser(user);
    return ResultCode.SUCCESS_DELETE_USER;
  }


  @Override
  public ResultResponse getUserInfo(HttpServletRequest request) {
    UserEntity user = userAccessHandler
        .findByUserId(jwtUtil.getUserId(request.getHeader("Access-Token")));

    return new ResultResponse(SUCCESS_GET_USER_INFO,
        new UserInfoDTO(user.getEmail(), user.getNickname()));
  }


  private Cookie createCookie(String value) {
    Cookie cookie = new Cookie("Refresh-Token", value);
    cookie.setMaxAge(24 * 60 * 60);
    cookie.setHttpOnly(true);

    return cookie;
  }

}

