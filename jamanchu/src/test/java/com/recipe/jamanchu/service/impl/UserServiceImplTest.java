package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.exceptions.exception.DuplicatedEmailException;
import com.recipe.jamanchu.exceptions.exception.DuplicatedNicknameException;
import com.recipe.jamanchu.model.dto.request.SignupDTO;
import com.recipe.jamanchu.model.dto.response.UserResponse;
import com.recipe.jamanchu.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userServiceimpl;

  @Test
  @DisplayName("회원가입 성공")
  void signup() {

    // given
    SignupDTO signup = new SignupDTO("dlrkdhsoff@gmail.com", "1234", "nickname");

    when(userRepository.existsByEmail(signup.getEmail())).thenReturn(false);
    when(userRepository.existsByNickname(signup.getNickname())).thenReturn(false);

    // when
    UserResponse result = userServiceimpl.signup(signup);

    // then
    assertEquals(result, UserResponse.SUCCESS_SIGNUP);
  }

  @Test
  @DisplayName("회원가입 실패 : 중복된 이메일")
  void signupDuplicationEmail() {

    // given
    SignupDTO signup = new SignupDTO("dlrkdhsoff@gmail.com", "1234", "nickname");
    UserResponse result = userServiceimpl.signup(signup);

    SignupDTO newUser = new SignupDTO("dlrkdhsoff@gmail.com", "1234", "nickname");
    when(userRepository.existsByEmail(signup.getEmail())).thenReturn(true);

    // when then
    assertThrows(DuplicatedEmailException.class, () -> userServiceimpl.signup(newUser));
  }

  @Test
  @DisplayName("회원가입 실패 : 중복된 닉네임")
  void signupDuplicationNickname() {

    // given
    SignupDTO signup = new SignupDTO("dlrkdhsoff@gmail.com", "1234", "nickname");
    UserResponse result = userServiceimpl.signup(signup);

    SignupDTO newUser = new SignupDTO("newEmail@gmail.com", "1234", "nickname");
    when(userRepository.existsByNickname(signup.getNickname())).thenReturn(true);

    // when then
    assertThrows(DuplicatedNicknameException.class, () -> userServiceimpl.signup(newUser));
  }
}
