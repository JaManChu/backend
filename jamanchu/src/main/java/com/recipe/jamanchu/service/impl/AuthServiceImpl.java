package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserAccessHandler userAccessHandler;

  @Override
  public ResultCode checkEmail(String email) {

    return userAccessHandler.existsByEmail(email);
  }

  @Override
  public ResultCode checkNickname(String nickname) {
    return userAccessHandler.existsByNickname(nickname);
  }
}
