package com.recipe.jamanchu.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidateUserTest {
  @Mock
  private ValidateUser validateUser;

  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // UserEntity 초기화
    userEntity = UserEntity.builder()
        .userId(1L)
        .email("test@example.com")
        .nickname("TestUser")
        .build();
  }

  @Test
  @DisplayName("존재하는 userId")
  void existsUserId() {
    // given
    when(validateUser.validateUserId(1L)).thenReturn(userEntity);

    // when
    UserEntity result = validateUser.validateUserId(1L);

    // then
    assertNotNull(result);
    assertEquals(userEntity, result);
  }

  @Test
  @DisplayName("존재하지 않은 userId")
  void userNotFoundByUserId() {
    // given
    when(validateUser.validateUserId(2L)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class, () -> validateUser.validateUserId(2L));
  }

  @Test
  @DisplayName("존재하는 email")
  void existsEmail() {
    // given
    when(validateUser.validateEmail("test@example.com")).thenReturn(userEntity);

    // when
    UserEntity result = validateUser.validateEmail("test@example.com");

    // then
    assertNotNull(result);
    assertEquals(userEntity, result);
  }

  @Test
  @DisplayName("존재하지 않은 email")
  void userNotFoundByEmail() {
    // given
    when(validateUser.validateEmail("test@example.com")).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class, () -> validateUser.validateEmail("test@example.com"));
  }
}