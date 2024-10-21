package com.recipe.jamanchu.api.auth.service;

import static org.mockito.Mockito.when;

import com.recipe.jamanchu.api.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.type.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

  @Mock
  private UserAccessHandler userAccessHandler;

  @InjectMocks
  private CustomUserDetailService userDetailService;

  @DisplayName("유저 객체를 DTO로 변환")
  @Test
  void UserEntityToUserDetailDTO() {
    //given
    Long userId = 1L;

    UserEntity user = UserEntity.builder()
        .userId(userId)
        .email("email")
        .nickname("nickname")
        .role(UserRole.USER)
        .password("password")
        .provider(null)
        .build();
    //when
    when(userAccessHandler.findByUserId(userId)).thenReturn(user);

    //then
    assertEquals(
        user.getEmail(),
        userDetailService.loadUserByUsername(userId.toString()).getUsername()
    );
  }

}