package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.DuplicatedEmailException;
import com.recipe.jamanchu.exceptions.exception.DuplicatedNicknameException;
import com.recipe.jamanchu.exceptions.exception.PasswordMismatchException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.dto.request.auth.DeleteUserDTO;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.auth.UserInfoDTO;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.UserRole;
import jakarta.servlet.http.HttpServletRequest;
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
  private JwtUtil jwtUtil;

  @Mock
  private HttpServletRequest request;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userServiceimpl;

  private static final Long USERID = 1L;
  private static final String EMAIL = "test@email.com";
  private static final String NICKNAME = "nickname";
  private static final String PASSWORD = "1234";
  private static final String PROVIDER = "kakao";
  private static final String BEFORE_PASSWORD = "oldPassword";
  private static final String AFTER_PASSWORD = "newPassword";
  private static final String NEW_NICKNAME = "newNickName";

  private SignupDTO signup;
  private UserUpdateDTO userUpdateDTO;
  private UserEntity user;
  private UserEntity kakaoUser;
  private DeleteUserDTO deleteUserDTO;
  private UserInfoDTO userInfoDTO;

  @BeforeEach
  void setUp() {
    signup = new SignupDTO(EMAIL, PASSWORD, NICKNAME);
    userUpdateDTO = new UserUpdateDTO(NEW_NICKNAME, BEFORE_PASSWORD, AFTER_PASSWORD);
    deleteUserDTO = new DeleteUserDTO(PASSWORD);
    userInfoDTO = new UserInfoDTO(EMAIL, NICKNAME);


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
    doThrow(new DuplicatedEmailException()).when(userAccessHandler)
        .existsByEmail(signup.getEmail());

    // when then
    assertThrows(DuplicatedEmailException.class, () -> userServiceimpl.signup(signup));
  }

  @Test
  @DisplayName("회원가입 실패 : 중복된 닉네임")
  void signup_DuplicationNickname() {
    // given
    doThrow(new DuplicatedNicknameException()).when(userAccessHandler)
        .existsByNickname(signup.getNickname());

    // when then
    assertThrows(DuplicatedNicknameException.class, () -> userServiceimpl.signup(signup));
  }

  @Test
  @DisplayName("회원정보 수정 성공 - 닉네임, 패스워드 모두 변경")
  void updateUserInfo_SuccessForPasswordAndNickname() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    doNothing().when(userAccessHandler).validatePassword(user.getPassword(), BEFORE_PASSWORD);
    doNothing().when(userAccessHandler).isSocialUser(user.getProvider());
    doNothing().when(userAccessHandler).existsByNickname(userUpdateDTO.getNickname());

    // when
    ResultCode result = userServiceimpl.updateUserInfo(request, userUpdateDTO);

    // then
    assertEquals(ResultCode.SUCCESS_UPDATE_USER_INFO, result);
  }

  @Test
  @DisplayName("회원정보 수정 성공 - 패스워드만 변경")
  void updateUserInfo_SuccessForPassword() {
    // given
    UserUpdateDTO userUpdateDTO = new UserUpdateDTO("nickname", BEFORE_PASSWORD, AFTER_PASSWORD);

    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    doNothing().when(userAccessHandler).validatePassword(user.getPassword(), BEFORE_PASSWORD);
    doNothing().when(userAccessHandler).isSocialUser(user.getProvider());
    // when
    ResultCode result = userServiceimpl.updateUserInfo(request, userUpdateDTO);

    // then
    assertEquals(ResultCode.SUCCESS_UPDATE_USER_INFO, result);
  }

  @Test
  @DisplayName("회원정보 수정 실패 : 존재하지 않은 회원인 경우")
  void updateUserInfo_NotFoundUser() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> userServiceimpl.updateUserInfo(request, userUpdateDTO));
  }

  @Test
  @DisplayName("회원정보 수정 실패 : 비밀번호가 일치하지 않은 경우")
  void updateUserInfo_PasswordMisMatch() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    doThrow(new PasswordMismatchException()).when(userAccessHandler)
        .validatePassword(user.getPassword(), BEFORE_PASSWORD);

    // when then
    assertThrows(PasswordMismatchException.class,
        () -> userServiceimpl.updateUserInfo(request, userUpdateDTO));
  }

  @Test
  @DisplayName("회원정보 수정 실패 : 카카오로 로그인을 한 회원")
  void updateUserInfo_SocialAccountException() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(kakaoUser);

    doNothing().when(userAccessHandler).validatePassword(user.getPassword(), BEFORE_PASSWORD);

    doThrow(new SocialAccountException()).when(userAccessHandler).isSocialUser(kakaoUser.getProvider());

    // when then
    assertThrows(SocialAccountException.class,
        () -> userServiceimpl.updateUserInfo(request, userUpdateDTO));
  }

  @Test
  @DisplayName("회원정보 수정 실패 : 닉네임이 중복인 경우")
  void updateUserInfo_DuplicationNickname() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    doNothing().when(userAccessHandler).validatePassword(user.getPassword(), BEFORE_PASSWORD);
    doNothing().when(userAccessHandler).isSocialUser(user.getProvider());

    doThrow(new DuplicatedNicknameException()).when(userAccessHandler)
        .existsByNickname(userUpdateDTO.getNickname());

    // when then
    assertThrows(DuplicatedNicknameException.class,
        () -> userServiceimpl.updateUserInfo(request, userUpdateDTO));
  }

  @Test
  @DisplayName("회원 탈퇴 성공 : 일반 회원")
  void deleteUser_Success() {

    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);
    doNothing().when(userAccessHandler)
        .validatePassword(user.getPassword(), deleteUserDTO.getPassword());

    // when
    ResultCode result = userServiceimpl.deleteUser(request, deleteUserDTO);

    // then
    assertEquals(ResultCode.SUCCESS_DELETE_USER, result);
  }

  @Test
  @DisplayName("회원 탈퇴 성공 : 소셜 가입을 한 회원")
  void deleteUser_Success_SocialAccount() {

    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(kakaoUser);

    // when
    ResultCode result = userServiceimpl.deleteUser(request, deleteUserDTO);

    // then
    assertEquals(ResultCode.SUCCESS_DELETE_USER, result);
  }

  @Test
  @DisplayName("회원 탈퇴 실패 : 존재하지 않은 회원인 경우")
  void deleteUser_NotFoundUser() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> userServiceimpl.deleteUser(request, deleteUserDTO));
  }

  @Test
  @DisplayName("회원 탈퇴 실패 : 비밀번호가 일치하지 않은 경우")
  void deleteUser_PasswordMisMatch() {

    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    doThrow(new PasswordMismatchException()).when(userAccessHandler)
        .validatePassword(user.getPassword(), deleteUserDTO.getPassword());

    // when then
    assertThrows(PasswordMismatchException.class,
        () -> userServiceimpl.deleteUser(request, deleteUserDTO));
  }

  @Test
  @DisplayName("회원 정보 조회 성공")
  void getUserInfo_Success() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    // when
    ResultResponse response = userServiceimpl.getUserInfo(request);

    // then
    assertEquals(ResultCode.SUCCESS_GET_USER_INFO.getStatusCode(), response.getCode());
    assertEquals(userInfoDTO.getEmail(), ((UserInfoDTO)response.getData()).getEmail());
    assertEquals(userInfoDTO.getNickname(), ((UserInfoDTO)response.getData()).getNickname());
  }

  @Test
  @DisplayName("회원 정보 조회 실패 : 존재하지 않은 사용자")
  void getUserInfo_NotFoundUser() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> userServiceimpl.getUserInfo(request));
  }

  @DisplayName("유저 객체를 DTO로 변환")
  @Test
  void UserEntityToUserDetailDTO() {
    //given
    String email = "test@gmail.com";

    UserEntity user = UserEntity.builder()
        .email(email)
        .nickname("nickname")
        .role(UserRole.USER)
        .password("password")
        .provider(null)
        .build();
    //when
    when(userAccessHandler.findByEmail(email)).thenReturn(user);

    //then
    assertEquals(
        user.getEmail(),
        userServiceimpl.loadUserByUsername(email).getUsername()
    );
  }
}