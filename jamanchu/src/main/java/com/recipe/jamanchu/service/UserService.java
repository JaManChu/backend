package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.request.SignupDTO;
import com.recipe.jamanchu.model.type.ResultCode;

public interface UserService {

  ResultCode signup(SignupDTO signupDTO);

}
