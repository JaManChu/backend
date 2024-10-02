package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<ResultCode> signup(SignupDTO signupDTO) {

    return ResponseEntity.ok(userService.signup(signupDTO));
  }

//  @GetMapping("/test")
//  public ResponseEntity<?> test(@RequestParam("access") String access,
//      @RequestParam("refresh") String refresh) {
//
//    log.info("access : {}", access);
//    log.info("refresh : {}", refresh);
//
//    return ResponseEntity.ok().body(access);
//  }
}