package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.request.SignupDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<ResultCode> signup(SignupDTO signupDTO) {

    return ResponseEntity.ok(userService.signup(signupDTO));
  }
}