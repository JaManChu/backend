package com.recipe.jamanchu.api.controller;

import com.recipe.jamanchu.core.exceptions.exception.MissingEmailException;
import com.recipe.jamanchu.core.exceptions.exception.MissingNicknameException;
import com.recipe.jamanchu.domain.model.dto.request.auth.PasswordCheckDTO;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;


  @GetMapping("/email-check")
  public ResponseEntity<ResultResponse> checkEmail(
      @RequestParam(name = "email", required = false) String email) {

    if (email == null) {
      throw new MissingEmailException();
    }
    return ResponseEntity.ok(authService.checkEmail(email));
  }

  @GetMapping("/nickname-check")
  public ResponseEntity<ResultResponse> checkNickname(
      @RequestParam(name = "nickname", required = false) String nickname) {

    if (nickname == null) {
      throw new MissingNicknameException();
    }
    return ResponseEntity.ok(authService.checkNickname(nickname));
  }

  @GetMapping("/token/refresh")
  public ResponseEntity<ResultResponse> refreshToken(HttpServletRequest request,
      HttpServletResponse response) {

    return ResponseEntity.ok(authService.refreshToken(request, response));
  }

  @PostMapping("/password-check")
  public ResponseEntity<ResultResponse> checkPassword(
      @Valid @RequestBody PasswordCheckDTO passwordCheckDTO,
      HttpServletRequest request) {

    return ResponseEntity.ok(authService.checkPassword(passwordCheckDTO, request));
  }

  @PostMapping("/find-password")
  public ResponseEntity<ResultResponse> findPassword(
      @RequestParam(name = "email") String email,
      @RequestParam(name = "nickname") String nickname
  ) {
    return ResponseEntity.ok(authService.findPassword(email, nickname));
  }

  @PostMapping("/update-password")
  public ResponseEntity<ResultResponse> updatePassword(
      @RequestParam(name = "userId") Long userId,
      @RequestParam(name = "password") String password
  ) {
    return ResponseEntity.ok(authService.updatePassword(userId, password));
  }
}
