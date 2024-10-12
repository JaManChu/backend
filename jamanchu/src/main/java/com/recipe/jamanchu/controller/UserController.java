package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.request.auth.DeleteUserDTO;
import com.recipe.jamanchu.model.dto.request.auth.LoginDTO;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  // 회원 가입
  @PostMapping("/signup")
  public ResponseEntity<ResultResponse> signup(@Valid @RequestBody SignupDTO signupDTO) {

    ResultResponse response = ResultResponse.of(userService.signup(signupDTO));
    return ResponseEntity.status(response.getCode()).body(response);
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<ResultResponse> login(@Valid @RequestBody LoginDTO loginDTO,
      HttpServletResponse response) {

    return ResponseEntity.ok(userService.login(loginDTO, response));
  }

  // OAuth signup & login from Kakao
  @GetMapping("/login/auth/kakako")
  public ResponseEntity<ResultResponse> kakaoLogin(@RequestParam String code,
      HttpServletResponse response) {

    return ResponseEntity.ok().body(userService.kakaoLogin(code, response));
  }

  // 회원 정보 수정
  @PutMapping
  public ResponseEntity<ResultResponse> updateUser(HttpServletRequest request,
      @Valid @RequestBody UserUpdateDTO userUpdateDTO) {

    return ResponseEntity.ok()
        .body(ResultResponse.of(userService.updateUserInfo(request, userUpdateDTO)));
  }

  // 회원 탈퇴
  @DeleteMapping
  public ResponseEntity<ResultResponse> deleteUser(HttpServletRequest request,
      @Valid @RequestBody DeleteUserDTO deleteUserDTO) {

    return ResponseEntity.ok()
        .body(ResultResponse.of(userService.deleteUser(request, deleteUserDTO)));
  }

  // 회원 정보 조회
  @GetMapping
  public ResponseEntity<ResultResponse> getUserInfo(HttpServletRequest request) {

    return ResponseEntity.ok()
        .body(userService.getUserInfo(request));
  }
}

