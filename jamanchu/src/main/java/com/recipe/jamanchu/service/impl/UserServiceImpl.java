package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.DuplicatedEmailException;
import com.recipe.jamanchu.exceptions.exception.DuplicatedNicknameException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserDetailsDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.UserRepository;
import com.recipe.jamanchu.service.UserService;
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

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  @Override
  public ResultCode signup(SignupDTO signupDTO) {
    if (userRepository.existsByEmail(signupDTO.getEmail())) {
      throw new DuplicatedEmailException();
    }

    if (userRepository.existsByNickname(signupDTO.getNickname())) {
      throw new DuplicatedNicknameException();
    }

    String enPassword = passwordEncoder.encode(signupDTO.getPassword());
    userRepository.save(UserEntity.builder()
        .email(signupDTO.getEmail())
        .password(enPassword)
        .nickname(signupDTO.getNickname())
        .role(UserRole.USER)
        .build());

    return ResultCode.SUCCESS_SIGNUP;
  }

  @Override
  public UserDetails loadUserByUsername(String email) {

    log.info("loadUserByUsername -> email : {}", email);
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(UserNotFoundException::new);

    return new UserDetailsDTO(user);
  }

  @Override
  public ResultCode updateUserInfo(UserUpdateDTO userUpdateDTO) {
    UserEntity user = userRepository.findByUserId(userUpdateDTO.getUserId())
        .orElseThrow(UserNotFoundException::new);

    //OAuth으로 로그인한 경우
    if (user.getProvider() != null) {
      throw new SocialAccountException();
    }

    userRepository.save(UserEntity.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .password(passwordEncoder.encode(userUpdateDTO.getPassword()))
        .nickname(userUpdateDTO.getNickname())
        .role(user.getRole())
        .build());

    return ResultCode.SUCCESS_UPDATE_USER_INFO;
  }
}
