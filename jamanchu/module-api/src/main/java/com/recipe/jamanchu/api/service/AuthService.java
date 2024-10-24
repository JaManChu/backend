package com.recipe.jamanchu.api.service;

import com.recipe.jamanchu.domain.model.dto.request.auth.PasswordCheckDTO;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

  ResultResponse checkEmail(String email);

  ResultResponse checkNickname(String nickname);

  ResultResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

  ResultResponse checkPassword(PasswordCheckDTO passwordCheckDTO, HttpServletRequest request);

  ResultResponse findPassword(String email, String nickname);

  ResultResponse updatePassword(Long userId, String password);
}
