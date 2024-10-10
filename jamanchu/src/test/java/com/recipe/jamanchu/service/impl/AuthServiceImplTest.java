package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.model.type.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private UserAccessHandler userAccessHandler;

  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  @DisplayName("checkEmail : 이미 사용 중인 이메일입니다.")
  void checkEmail_Email_Already_In_Use() {

    // given
    String email = "test@example.com";
    ResultCode resultCode = ResultCode.EMAIL_ALREADY_IN_USE;
    when(userAccessHandler.existsByEmail(email)).thenReturn(resultCode);

    // when
    ResultCode result = authService.checkEmail(email);

    assertEquals(resultCode, result);

  }

  @Test
  @DisplayName("checkEmail : 사용할 수 있는 이메일입니다.")
  void checkEmail_Email_Available() {

    // given
    String email = "test@example.com";
    ResultCode resultCode = ResultCode.EMAIL_AVAILABLE;
    when(userAccessHandler.existsByEmail(email)).thenReturn(resultCode);

    // when
    ResultCode result = authService.checkEmail(email);

    assertEquals(resultCode, result);
  }
}