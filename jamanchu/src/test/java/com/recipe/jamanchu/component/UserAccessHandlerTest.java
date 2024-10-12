package com.recipe.jamanchu.component;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.oauth2.KakaoUserDetails;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.DuplicatedNicknameException;
import com.recipe.jamanchu.exceptions.exception.PasswordMismatchException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAccessHandlerTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private UserAccessHandler userAccessHandler;

  private UserEntity user;
  private KakaoUserDetails kakaoUserDetails;

  @BeforeEach
  void setUp() {
    user = UserEntity.builder()
        .userId(1L)
        .email("test@example.com")
        .nickname("testNickname")
        .password("encodedPassword")
        .provider("kakao")
        .providerId("providerId")
        .role(com.recipe.jamanchu.model.type.UserRole.USER)
        .build();

    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", "test@example.com");

    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", "testNickname");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", "providerId");
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", properties);

    kakaoUserDetails = new KakaoUserDetails(attributes);
  }

  @Test
  @DisplayName("findByUserId - 성공: 사용자 존재")
  void testFindByUserId_Success() {
    // given
    Long userId = 1L;
    when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

    // when
    UserEntity result = userAccessHandler.findByUserId(userId);

    // then
    assertNotNull(result);
    assertEquals(userId, result.getUserId());
  }

  @Test
  @DisplayName("findByUserId - 실패: 사용자 없음")
  void testFindByUserId_UserNotFound() {
    // given
    Long userId = 2L;
    when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> userAccessHandler.findByUserId(userId));
    verify(userRepository, times(1)).findByUserId(userId);
  }

  @Test
  @DisplayName("findByEmail - 성공: 사용자 존재")
  void findByEmail_Success() {
    // given
    String email = "test@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    // when
    UserEntity result = userAccessHandler.findByEmail(email);

    // then
    assertNotNull(result);
    assertEquals(email, result.getEmail());
    verify(userRepository, times(1)).findByEmail(email);
  }

  @Test
  @DisplayName("findByEmail - 실패: 사용자 없음")
  void findByEmail_UserNotFound() {
    // given
    String email = "nonexistent@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> userAccessHandler.findByEmail(email));
    verify(userRepository, times(1)).findByEmail(email);
  }

  @Test
  @DisplayName("findOrCreateUser - 성공: 사용자 존재")
  void findOrCreateUser_UserExists() {
    // given
    when(userRepository.findByEmail(kakaoUserDetails.getEmail())).thenReturn(Optional.of(user));

    // when
    UserEntity result = userAccessHandler.findOrCreateUser(kakaoUserDetails);

    // then
    assertNotNull(result);
    assertEquals(user.getEmail(), result.getEmail());
    verify(userRepository, times(1)).findByEmail(kakaoUserDetails.getEmail());
    verify(userRepository, times(0)).save(any(UserEntity.class));
  }

  @Test
  @DisplayName("findOrCreateUser - 성공: 사용자 없음, 생성")
  void findOrCreateUser_UserDoesNotExist() {
    // given
    when(userRepository.findByEmail(kakaoUserDetails.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encodedRandomPassword");
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);

    // when
    UserEntity result = userAccessHandler.findOrCreateUser(kakaoUserDetails);

    // then
    assertNotNull(result);
    assertEquals(user, result);
  }

  @Test
  @DisplayName("existsById - 성공 : 사용자 존재")
  void existsById_Success() {
    // given
    Long userId = 1L;
    when(userRepository.existsById(userId)).thenReturn(true);

    // when & then
    assertDoesNotThrow(() -> userAccessHandler.existsById(userId));
  }

  @Test
  @DisplayName("existsById - 실패 : 존재하지 않은 사용자")
  void existsById_UserNotFound() {
    // given
    Long userId = 1L;
    when(userRepository.existsById(userId)).thenReturn(false);

    // when & then
    assertThrows(UserNotFoundException.class, () -> userAccessHandler.existsById(userId));
  }

  @Test
  @DisplayName("existsByEmail :  이미 사용 중인 이메일입니다.")
  void existsByEmail_Duplicated() {
    // given
    String email = "test@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(true);

    // when
    ResultCode resultCode = userAccessHandler.existsByEmail(email);

    // then
    assertEquals(ResultCode.EMAIL_ALREADY_IN_USE, resultCode);
  }

  @Test
  @DisplayName("existsByEmail : 사용할 수 있는 이메일입니다.")
  void existsByEmail_NotDuplicated() {
    // given
    String email = "test@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(false);

    // when
    ResultCode resultCode = userAccessHandler.existsByEmail(email);

    // then
    assertEquals(ResultCode.EMAIL_AVAILABLE, resultCode);
  }

  @Test
  @DisplayName("existsByNickname - 실패: 닉네임 중복")
  void existsByNickname_Duplicated() {
    // given
    String nickname = "testNickname";
    when(userRepository.existsByNickname(nickname)).thenReturn(true);

    // when & then
    assertThrows(DuplicatedNicknameException.class, () -> userAccessHandler.existsByNickname(nickname));
    verify(userRepository, times(1)).existsByNickname(nickname);
  }

  @Test
  @DisplayName("existsByNickname - 성공: 닉네임 미중복")
  void existsByNickname_NotDuplicated() {
    // given
    String nickname = "uniqueNickname";
    when(userRepository.existsByNickname(nickname)).thenReturn(false);

    // when & then
    assertDoesNotThrow(() -> userAccessHandler.existsByNickname(nickname));
  }

  @Test
  @DisplayName("isSocialUser - 실패: 소셜 계정")
  void isSocialUser_WithProvider() {
    // given
    String provider = "kakao";

    // when & then
    assertThrows(SocialAccountException.class, () -> userAccessHandler.isSocialUser(provider));
  }

  @Test
  @DisplayName("isSocialUser - 성공: 소셜 계정 아님")
  void isSocialUser_NoProvider() {
    // given
    String provider = null;

    // when & then
    assertDoesNotThrow(() -> userAccessHandler.isSocialUser(provider));
  }

  @Test
  @DisplayName("validatePassword - 실패 : 비밀번호 불일치")
  void validatePassword_PasswordMisMatch() {
    // given
    String otherPassword = "otherPassword";
    String userPassword = passwordEncoder.encode("userPassword");

    // when & then
    assertThrows(PasswordMismatchException.class,
        () -> userAccessHandler.validatePassword(userPassword, otherPassword));
  }

  @Test
  @DisplayName("validatePassword - 성공 : 비밀번호 일치")
  void validatePassword_PasswordMatch() {
    // given
    String rawPassword = "password";
    String encodedPassword = passwordEncoder.encode("password");
    when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

    // when & then
    assertDoesNotThrow(() -> userAccessHandler.validatePassword(encodedPassword, rawPassword));
  }

  @Test
  @DisplayName("saveUser - 성공: 사용자 저장")
  void saveUser_Success() {
    // given
    when(userRepository.save(user)).thenReturn(user);

    // when
    UserEntity newUser = userRepository.save(user);

    // then
    assertEquals(newUser, user);
    assertEquals(newUser.getUserId(), user.getUserId());
    assertEquals(newUser.getNickname(), user.getNickname());
  }

}