package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.GlobalException;
import com.recipe.jamanchu.model.dto.request.SignupDTO;
import com.recipe.jamanchu.model.dto.response.UserResponse;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.UserRepository;
import com.recipe.jamanchu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  @Override
  public UserResponse signup(SignupDTO signupDTO) {
    if (userRepository.existsByEmail(signupDTO.getEmail())) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다.");
    }

    if (userRepository.existsByNickname(signupDTO.getNickname())) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임 입니다.");
    }

    String enPassword = passwordEncoder.encode(signupDTO.getPassword());
    userRepository.save(UserEntity.builder()
        .email(signupDTO.getEmail())
        .password(enPassword)
        .nickname(signupDTO.getNickname())
        .role(UserRole.USER)
        .build());

    return UserResponse.SUCCESS_SIGNUP;
  }
}
