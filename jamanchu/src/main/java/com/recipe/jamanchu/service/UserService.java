package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.request.SignupDTO;
import com.recipe.jamanchu.model.dto.response.UserResponse;

public interface UserService {

  UserResponse signup(SignupDTO signupDTO);

}
