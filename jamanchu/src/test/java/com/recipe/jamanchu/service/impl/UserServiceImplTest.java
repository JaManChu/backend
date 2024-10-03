package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.DuplicatedEmailException;
import com.recipe.jamanchu.exceptions.exception.DuplicatedNicknameException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.UserRepository;
import java.util.Optional;
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
    ResultCode result = userServiceimpl.signup(signup);

    // then
    assertEquals(result, ResultCode.SUCCESS_SIGNUP);
  }

  @Test
  @DisplayName("회원가입 실패 : 중복된 이메일")
  void signupDuplicationEmail() {

    // given
    SignupDTO signup = new SignupDTO("dlrkdhsoff@gmail.com", "1234", "nickname");
    ResultCode result = userServiceimpl.signup(signup);

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
    ResultCode result = userServiceimpl.signup(signup);

    SignupDTO newUser = new SignupDTO("newEmail@gmail.com", "1234", "nickname");
    when(userRepository.existsByNickname(signup.getNickname())).thenReturn(true);

    // when then
    assertThrows(DuplicatedNicknameException.class, () -> userServiceimpl.signup(newUser));
  }

  @Test
  @DisplayName("회원정보 수정 성공")
  void updateUserInfo_Success() {
    // given
    UserUpdateDTO userUpdateDTO = new UserUpdateDTO(1L, "newPassword", "newNickName");

    UserEntity userEntity = UserEntity.builder()
        .userId(1L)
        .email("test@example.com")
        .nickname("TestUser")
        .role(UserRole.USER)
        .password("password")
        .provider(null)
        .build();

    when(userRepository.findByUserId(1L)).thenReturn(Optional.of(userEntity));
    when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

    // 테스트 실행
    ResultCode result = userServiceimpl.updateUserInfo(userUpdateDTO);

    // 검증
    assertEquals(ResultCode.SUCCESS_UPDATE_USER_INFO, result);
  }

  @Test
  @DisplayName("회원정보 수정 실패 : OAuth 회원가입을 한 회원")
  void updateUserInfo_SocialAccountException() {
    // given
    UserUpdateDTO userUpdateDTO = new UserUpdateDTO(1L, "newPassword", "newNickName");

    UserEntity userEntity = UserEntity.builder()
        .userId(1L)
        .email("test@example.com")
        .nickname("TestUser")
        .role(UserRole.USER)
        .password("password")
        .provider("KAKAO")
        .build();

    when(userRepository.findByUserId(1L)).thenReturn(Optional.of(userEntity));

    // when then
    assertThrows(SocialAccountException.class, () -> userServiceimpl.updateUserInfo(userUpdateDTO));
  }
}
