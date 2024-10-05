package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<ResultResponse> signup(SignupDTO signupDTO) {

    return ResponseEntity.ok()
        .body(ResultResponse.of(userService.signup(signupDTO)));
  }

  @GetMapping("/test")
  public ResponseEntity<?> test(@RequestParam("access") String access,
      @RequestParam("refresh") String refresh) {

    log.info("method -> test");

    log.info("access : {}", access);
    log.info("refresh : {}", refresh);

    return ResponseEntity.ok().body("로그인 성공");
  }

  @PutMapping
  public ResponseEntity<ResultResponse> updateUser(UserUpdateDTO userUpdateDTO) {

    return ResponseEntity.ok()
        .body(ResultResponse.of(userService.updateUserInfo(userUpdateDTO)));
  }
}
