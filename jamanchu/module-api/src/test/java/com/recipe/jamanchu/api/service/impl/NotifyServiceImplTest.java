package com.recipe.jamanchu.api.service.impl;

import static com.recipe.jamanchu.domain.model.type.TokenType.ACCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.api.auth.jwt.JwtUtil;
import com.recipe.jamanchu.api.notify.IgnoreRecipeCommentAlarmMap;
import com.recipe.jamanchu.api.notify.SseEmitterMap;
import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeRatingEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.dto.response.notify.Notify;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@DisplayName("알림 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class NotifyServiceImplTest {

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private HttpServletRequest request;

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private SseEmitter sseEmitter;

  @Mock
  private SseEmitterMap subscribers;

  @Mock
  private IgnoreRecipeCommentAlarmMap ignoreAlarmRecipeIds;

  @InjectMocks
  private NotifyServiceImpl notifyService;

  @DisplayName("Access-Token을 통한 알림 구독 성공")
  @Test
  void successSubscribe() {
    // given
    Long userId = 1L;
    // when
    when(jwtUtil.getUserId(request.getHeader("Access-Token"))).thenReturn(userId);
    doNothing().when(userAccessHandler).existsById(1L);

    // action
    SseEmitter subscribe = notifyService.subscribe(request);

    // then
    verify(subscribers, atLeastOnce()).put(userId, subscribe);
  }

  @DisplayName("Access-Token을 통한 알림 구독 실패")
  @Test
  void failSubscribe() {
    // given
    Long userId = 1L;
    // when
    when(jwtUtil.getUserId(request.getHeader("Access-Token"))).thenReturn(userId);
    doNothing().when(userAccessHandler).existsById(1L);

    // action
    SseEmitter subscribe = notifyService.subscribe(request);

    // then
    verify(subscribers, atLeastOnce()).put(userId, subscribe);
  }

  @DisplayName("알림 전송 성공")
  @Test
  void successNotifyUser() {
    // given
    Long userId = 1L;

    UserEntity user = UserEntity.builder()
        .userId(userId)
        .build();

    RecipeEntity recipe = RecipeEntity.builder()
        .id(1L)
        .name("recipe")
        .rating(List.of(
                RecipeRatingEntity.builder().rating(5.0).build()
            )
        )
        .user(user)
        .build();

    Notify notify = Notify.of("recipe", "message", 5.0, "commentUser");

    // when
    doNothing().when(userAccessHandler).existsById(1L);

    // action
    notifyService.notifyUser(recipe, userId, notify);

    // then
    verify(userAccessHandler, atLeastOnce()).existsById(userId);
  }

  @DisplayName("알림 무시 성공")
  @Test
  void successToggleRecipeComment() {
    // given
    Long userId = 1L;
    Long recipeId = 1L;

    // when
    when(jwtUtil.getUserId(request.getHeader("Access-Token"))).thenReturn(userId);
    doNothing().when(userAccessHandler).existsById(1L);

    // action
    notifyService.toggleRecipeComment(request, recipeId);

    // then
    verify(ignoreAlarmRecipeIds, atLeastOnce()).put(userId, Set.of(recipeId));
  }

  @DisplayName("알림 무시 후 전송을 받지 않는다.")
  @Test
  void NoReceiveNotifyAfterIgnoreRecipeNotify() {
    //given
    Long ignoreUserId = 1L;
    Long ignoreRecipeId = 1L;

    UserEntity ignoreUser = UserEntity.builder()
        .userId(ignoreUserId)
        .nickname("user")
        .build();


    Long userId = 2L;

    UserEntity user = UserEntity.builder()
        .userId(userId)
        .nickname("user")
        .build();

    RecipeEntity recipe = RecipeEntity.builder()
        .id(1L)
        .name("recipe")
        .rating(List.of(
                RecipeRatingEntity.builder().rating(5.0).build()
            )
        )
        .user(ignoreUser)
        .build();

    Notify notify = Notify.of("recipe", "message", 5.0, "user");

    // when
    when(jwtUtil.getUserId(request.getHeader(ACCESS.getValue()))).thenReturn(ignoreUserId);
    doNothing().when(userAccessHandler).existsById(1L);
    when(jwtUtil.getUserId(request.getHeader(ACCESS.getValue()))).thenReturn(ignoreUserId);
    when(userAccessHandler.findByUserId(ignoreUserId)).thenReturn(user);
    when(recipeRepository.findAllByUser(user)).thenReturn(Optional.of(List.of(recipe)));

    ResultResponse resultResponse1 = notifyService.toggleRecipeComment(request, ignoreRecipeId);
    Object data1 = resultResponse1.getData();
    assertEquals(1, ((List) data1).size());
    assertEquals(1L, ((List) data1).get(0));
//
    doNothing().when(userAccessHandler).existsById(2L);

    notifyService.notifyUser(recipe, userId, notify);

    //then
    verify(userAccessHandler, times(2)).existsById(any());
    verify(jwtUtil, times(2)).getUserId(request.getHeader(ACCESS.getValue()));
    verify(ignoreAlarmRecipeIds, times(2)).containsKey(ignoreUserId);
    verify(ignoreAlarmRecipeIds, atLeastOnce()).put(ignoreUserId, Set.of(ignoreRecipeId));
    verify(userAccessHandler, atLeastOnce()).findByUserId(anyLong());
    verify(recipeRepository, atLeastOnce()).findAllByUser(any());
  }

}