package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.type.ResultCode;

public interface AuthService {

  ResultCode checkEmail(String email);

  ResultCode checkNickname(String nickname);
}
