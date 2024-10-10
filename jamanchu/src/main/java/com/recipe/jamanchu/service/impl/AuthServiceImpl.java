package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.repository.UserRepository;
import com.recipe.jamanchu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;

  @Override
  public ResultCode checkEmail(String email) {

    if (userRepository.existsByEmail(email)) {
      return ResultCode.EMAIL_ALREADY_IN_USE;
    }

    return ResultCode.EMAIL_AVAILABLE;
  }
}
