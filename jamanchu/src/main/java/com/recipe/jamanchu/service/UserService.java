package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.request.auth.DeleteUserDTO;
import com.recipe.jamanchu.model.dto.request.auth.LoginDTO;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.type.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

  ResultCode signup(SignupDTO signupDTO);

  ResultCode updateUserInfo(HttpServletRequest request, UserUpdateDTO userUpdateDTO);

  ResultCode deleteUser(HttpServletRequest request, DeleteUserDTO deleteUserDTO);

  ResultResponse getUserInfo(HttpServletRequest request);

  ResultResponse login(LoginDTO loginDTO, HttpServletResponse response);

  ResultResponse kakaoLogin(String code, HttpServletResponse response);
}

