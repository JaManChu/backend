package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.request.auth.LoginDTO;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

  ResultResponse signup(SignupDTO signupDTO);

  ResultResponse updateUserInfo(HttpServletRequest request, UserUpdateDTO userUpdateDTO);

  ResultResponse deleteUser(HttpServletRequest request);

  ResultResponse getUserInfo(HttpServletRequest request);

  ResultResponse login(LoginDTO loginDTO, HttpServletResponse response);

  String kakaoLogin(String code, HttpServletResponse response);

  ResultResponse getUserRecipes(int myRecipePage, int scrapRecipePage, HttpServletRequest request);
}

