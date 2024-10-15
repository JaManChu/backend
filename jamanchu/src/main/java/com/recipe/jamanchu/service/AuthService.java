package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

  ResultResponse checkEmail(String email);

  ResultResponse checkNickname(String nickname);

  ResultResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
}
