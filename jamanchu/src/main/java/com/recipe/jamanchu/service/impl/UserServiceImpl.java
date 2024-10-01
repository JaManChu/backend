package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.DuplicatedEmailException;
import com.recipe.jamanchu.exceptions.exception.DuplicatedNicknameException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserDetailsDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.UserRepository;
import com.recipe.jamanchu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
  public UserDetails loadUserByUsername(String username) {
    UserEntity user = userRepository.findByEmail(username)
        .orElseThrow(UserNotFoundException::new);

    return new UserDetailsDTO(user);
  }
}
