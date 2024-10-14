package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.exceptions.exception.MissingEmailException;
import com.recipe.jamanchu.exceptions.exception.MissingNicknameException;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;


  @GetMapping("/email-check")
  public ResponseEntity<ResultResponse> checkEmail(@RequestParam(name = "email", required = false) String email) {
    if (email == null) {
      throw new MissingEmailException();
    }
    return ResponseEntity.ok(authService.checkEmail(email));
  }

  @GetMapping("/nickname-check")
  public ResponseEntity<ResultResponse> checkNickname(@RequestParam(name = "nickname", required = false) String nickname) {
    if (nickname == null) {
      throw new MissingNicknameException();
    }
    return ResponseEntity.ok(authService.checkNickname(nickname));
  }
}
