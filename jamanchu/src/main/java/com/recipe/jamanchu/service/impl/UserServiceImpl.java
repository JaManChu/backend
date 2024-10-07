package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.model.dto.request.auth.DeleteUserDTO;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserDetailsDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

  private final BCryptPasswordEncoder passwordEncoder;
  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;

  @Override
  public ResultCode signup(SignupDTO signupDTO) {
    // 이메일 중복 체크
    userAccessHandler.existsByEmail(signupDTO.getEmail());
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
  public UserDetails loadUserByUsername(String email) {

    log.info("loadUserByUsername -> email : {}", email);
    UserEntity user = userAccessHandler.findByEmail(email);

    return new UserDetailsDTO(user);
  }

  @Override
  public ResultCode updateUserInfo(HttpServletRequest request, UserUpdateDTO userUpdateDTO) {
    UserEntity user = userAccessHandler
        .findByUserId(jwtUtil.getUserId(request.getHeader("access-token")));

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
        .findByUserId(jwtUtil.getUserId(request.getHeader("access-token")));

    userAccessHandler.validatePassword(user.getPassword(), deleteUserDTO.getPassword());

    userAccessHandler.deleteUser(user);
    return ResultCode.SUCCESS_DELETE_USER;
  }
}

