package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.DuplicatedEmailException;
import com.recipe.jamanchu.exceptions.exception.DuplicatedNicknameException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.UserRole;
import org.junit.jupiter.api.BeforeEach;
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
  private UserAccessHandler userAccessHandler;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userServiceimpl;

  private static final String EMAIL = "test@email.com";
  private static final String NICKNAME = "nickname";
  private static final String PASSWORD = "1234";
  private static final String PROVIDER = "kakao";
  private static final String NEW_PASSWORD = "newPassword";
  private static final String NEW_NICKNAME = "newNickName";

  private SignupDTO signup;
  private UserUpdateDTO userUpdateDTO;
  private UserEntity user;
  private UserEntity kakaoUser;

  @BeforeEach
  void setUp() {
    signup = new SignupDTO(EMAIL, PASSWORD, NICKNAME);
    userUpdateDTO = new UserUpdateDTO(1L, NEW_PASSWORD, NEW_NICKNAME);

    // 일반 회원
    user = UserEntity.builder()
        .userId(1L)
        .email(EMAIL)
        .nickname(NICKNAME)
        .role(UserRole.USER)
        .password(PASSWORD)
        .provider(null)
        .build();

    // 카카오로 로그인한 회원
    kakaoUser = UserEntity.builder()
        .userId(1L)
        .email(EMAIL)
        .nickname(NICKNAME)
        .role(UserRole.USER)
        .password(PASSWORD)
        .provider(PROVIDER)
        .build();
  }

  @Test
  @DisplayName("회원가입 성공")
  void signup_Success() {
    // given
    doNothing().when(userAccessHandler).existsByEmail(signup.getEmail());
    doNothing().when(userAccessHandler).existsByNickname(signup.getNickname());

    // when
    ResultCode result = userServiceimpl.signup(signup);

    // then
    assertEquals(ResultCode.SUCCESS_SIGNUP, result);
  }

  @Test
  @DisplayName("회원가입 실패 : 중복된 이메일")
  void signup_DuplicationEmail() {
    // given
    doThrow(new DuplicatedEmailException()).when(userAccessHandler).existsByEmail(signup.getEmail());

    // when then
    assertThrows(DuplicatedEmailException.class, () -> userServiceimpl.signup(signup));
  }

  @Test
  @DisplayName("회원가입 실패 : 중복된 닉네임")
  void signup_DuplicationNickname() {
    // given
    doThrow(new DuplicatedNicknameException()).when(userAccessHandler).existsByNickname(signup.getNickname());

    // when then
    assertThrows(DuplicatedNicknameException.class, () -> userServiceimpl.signup(signup));
  }

  @Test
  @DisplayName("회원정보 수정 성공")
  void updateUserInfo_Success() {
    // given
    when(userAccessHandler.findByUserId(1L)).thenReturn(user);
    doNothing().when(userAccessHandler).isSocialUser(user.getProvider());
    doNothing().when(userAccessHandler).existsByNickname(userUpdateDTO.getNickname());

    // when
    ResultCode result = userServiceimpl.updateUserInfo(userUpdateDTO);

    // then
    assertEquals(ResultCode.SUCCESS_UPDATE_USER_INFO, result);
  }

  @Test
  @DisplayName("회원정보 수정 실패 : userId가 존재하지 않은 경우")
  void updateUserInfo_NotFoundUser() {
    // given
    when(userAccessHandler.findByUserId(1L)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class, () -> userServiceimpl.updateUserInfo(userUpdateDTO));
  }

  @Test
  @DisplayName("회원정보 수정 실패 : 카카오로 로그인을 한 회원")
  void updateUserInfo_SocialAccountException() {
    // given
    when(userAccessHandler.findByUserId(1L)).thenReturn(kakaoUser);
    doThrow(new SocialAccountException()).when(userAccessHandler).isSocialUser(kakaoUser.getProvider());

    // when then
    assertThrows(SocialAccountException.class, () -> userServiceimpl.updateUserInfo(userUpdateDTO));
  }

  @Test
  @DisplayName("회원정보 수정 실패 : 닉네임이 중복인 경우")
  void updateUserInfo_DuplicationNickname() {
    // given
    when(userAccessHandler.findByUserId(1L)).thenReturn(user);
    doNothing().when(userAccessHandler).isSocialUser(user.getProvider());
    doThrow(new DuplicatedNicknameException()).when(userAccessHandler).existsByNickname(userUpdateDTO.getNickname());

    // when then
    assertThrows(DuplicatedNicknameException.class, () -> userServiceimpl.updateUserInfo(userUpdateDTO));
  }
}
