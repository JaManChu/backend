package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.response.ResultResponse;

public interface AuthService {

  ResultResponse checkEmail(String email);

  ResultResponse checkNickname(String nickname);
}
