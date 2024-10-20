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
import com.recipe.jamanchu.exceptions.exception.PasswordMismatchException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.exceptions.exception.WithdrewUserException;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.repository.CommentRepository;
import com.recipe.jamanchu.repository.IngredientRatingRepository;
import com.recipe.jamanchu.repository.RecipeRatingRepository;
import com.recipe.jamanchu.repository.RecipeRepository;
import com.recipe.jamanchu.repository.ScrapedRecipeRepository;
import com.recipe.jamanchu.repository.UserRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAccessHandlerTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private ScrapedRecipeRepository scrapedRecipeRepository;

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private RecipeRatingRepository recipeRatingRepository;

  @Mock
  private IngredientRatingRepository ingredientRatingRepository;

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
  @DisplayName("findByEmail - 성공: 사용자 존재, 삭제 예정 없음")
  void findByEmail_Success() {
    // given
    String email = "test@example.com";
    UserEntity user = Mockito.mock(UserEntity.class);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(user.getDeletionScheduledAt()).thenReturn(null);

    // when
    UserEntity result = userAccessHandler.findByEmail(email);

    // then
    assertEquals(user, result);
  }

  @Test
  @DisplayName("findByEmail - 실패: 사용자 없음")
  void findByEmail_UserNotFound() {
    // given
    String email = "nonexistent@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> userAccessHandler.findByEmail(email));
  }

  @Test
  @DisplayName("findByEmail - 실패: 사용자 삭제 예정")
  void findByEmail_UserScheduledForDeletion() {
    // given
    String email = "deletion@example.com";
    UserEntity user = Mockito.mock(UserEntity.class);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(user.getDeletionScheduledAt()).thenReturn(Mockito.mock(LocalDate.class));

    // when & then
    assertThrows(UserNotFoundException.class, () -> userAccessHandler.findByEmail(email));
  }

  @Test
  @DisplayName("findOrCreateUser - 성공: 사용자 존재")
  void findOrCreateUser_UserExists() {
    // given
    when(userRepository.findKakaoUser(kakaoUserDetails.getEmail())).thenReturn(Optional.of(user));

    // when
    UserEntity result = userAccessHandler.findOrCreateUser(kakaoUserDetails);

    // then
    assertNotNull(result);
    assertEquals(user.getEmail(), result.getEmail());
    verify(userRepository, times(1)).findKakaoUser(kakaoUserDetails.getEmail());
    verify(userRepository, times(0)).save(any(UserEntity.class));
  }

  @Test
  @DisplayName("findOrCreateUser - 성공: 사용자 없음, 생성")
  void findOrCreateUser_UserDoesNotExist() {
    // given
    when(userRepository.findKakaoUser(kakaoUserDetails.getEmail())).thenReturn(Optional.empty());
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

    UserEntity user = Mockito.mock(UserEntity.class);
    when(userRepository.existsByEmail(email)).thenReturn(true);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(user.getDeletionScheduledAt()).thenReturn(null);

    // when
    ResultResponse resultResponse = userAccessHandler.existsByEmail(email);

    // then
    assertEquals(ResultCode.EMAIL_ALREADY_IN_USE.getStatusCode(), resultResponse.getCode());
  }

  @Test
  @DisplayName("existsByEmail : 사용할 수 있는 이메일입니다.")
  void existsByEmail_NotDuplicated() {
    // given
    String email = "test@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(false);

    // when
    ResultResponse resultResponse = userAccessHandler.existsByEmail(email);

    // then
    assertEquals(ResultCode.EMAIL_AVAILABLE.getStatusCode(), resultResponse.getCode());
  }

  @Test
  @DisplayName("existsByEmail : 탈퇴한 회원의 이메일")
  void existsByEmail_WithdrewUser() {
    // given
    String email = "test@example.com";

    UserEntity user = Mockito.mock(UserEntity.class);
    when(userRepository.existsByEmail(email)).thenReturn(true);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(user.getDeletionScheduledAt()).thenReturn(LocalDate.now());

    // when & then
    assertThrows(WithdrewUserException.class, () -> userAccessHandler.existsByEmail(email));
  }

  @Test
  @DisplayName("existsByNickname : 이미 사용중인 닉네임 입니다.")
  void existsByNickname_Duplicated() {
    // given
    String nickname = "nickname";
    when(userRepository.existsByNickname(nickname)).thenReturn(true);

    // when
    ResultResponse resultResponse = userAccessHandler.existsByNickname(nickname);

    // then
    assertEquals(ResultCode.NICKNAME_ALREADY_IN_USE.getStatusCode(), resultResponse.getCode());
  }

  @Test
  @DisplayName("existsByNickname : 사용할 수 있는 닉네임 입니다.")
  void existsByNickname_NotDuplicated() {
    // given
    String nickname = "nickname";
    when(userRepository.existsByNickname(nickname)).thenReturn(false);

    // when
    ResultResponse resultResponse = userAccessHandler.existsByNickname(nickname);

    // then
    assertEquals(ResultCode.NICKNAME_AVAILABLE.getStatusCode(), resultResponse.getCode());
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

  @Test
  @DisplayName("validatePassword : 비밀번호가 일치 하지 않습니다.")
  void validateBeforePW_MisMatch() {
    // given
    String rawPassword = "password";
    String encodedPassword = passwordEncoder.encode("otherPassword");
    when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

    // when
    ResultResponse resultResponse = userAccessHandler.validateBeforePW(encodedPassword, rawPassword);

    // then
    assertEquals(ResultCode.PASSWORD_MISMATCH.getStatusCode(), resultResponse.getCode());
  }

  @Test
  @DisplayName("validatePassword : 비밀번호가 일치 합니다.")
  void validateBeforePW_Match() {
    // given
    String rawPassword = "password";
    String encodedPassword = passwordEncoder.encode("password");
    when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

    // when
    ResultResponse resultResponse = userAccessHandler.validateBeforePW(encodedPassword, rawPassword);

    // then
    assertEquals(ResultCode.PASSWORD_MATCH.getStatusCode(), resultResponse.getCode());
  }

  @Test
  @DisplayName("testDeleteAllUserData : 탈퇴한 사용자의 모든 데이터 삭제")
  public void testDeleteAllUserData() {
    // 가짜 데이터 설정
    UserEntity user1 = new UserEntity(1L, "user1@example.com", "password", "user1", null, null, null, null);
    UserEntity user2 = new UserEntity(2L, "user2@example.com", "password", "user2", null, null, null, null);

    List<UserEntity> users = Arrays.asList(user1, user2);

    // findAllDeletedToday() 메서드가 users 리스트를 반환하도록 설정
    when(userRepository.findAllDeletedToday()).thenReturn(users);

    // deleteAllUserData() 실행
    userAccessHandler.deleteAllUserData();

    // 삭제된 데이터를 검증
    verify(scrapedRecipeRepository, times(1)).deleteAllByUser(user1);
    verify(scrapedRecipeRepository, times(1)).deleteAllByUser(user2);

    verify(commentRepository, times(1)).deleteAllByUser(user1);
    verify(commentRepository, times(1)).deleteAllByUser(user2);

    verify(recipeRatingRepository, times(1)).deleteAllByUser(user1);
    verify(recipeRatingRepository, times(1)).deleteAllByUser(user2);

    verify(ingredientRatingRepository, times(1)).deleteAllByUser(user1);
    verify(ingredientRatingRepository, times(1)).deleteAllByUser(user2);

    verify(recipeRepository, times(1)).deleteAllByUser(user1);
    verify(recipeRepository, times(1)).deleteAllByUser(user2);

    // user 삭제 검증
    verify(userRepository, times(1)).deleteUserByUserId(user1.getUserId());
    verify(userRepository, times(1)).deleteUserByUserId(user2.getUserId());
  }

}