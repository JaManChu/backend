package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserAccessHandler userAccessHandler;

  @Override
  public ResultResponse checkEmail(String email) {

    return userAccessHandler.existsByEmail(email);
  }

  @Override
  public ResultResponse checkNickname(String nickname) {

    return userAccessHandler.existsByNickname(nickname);
  }
}
