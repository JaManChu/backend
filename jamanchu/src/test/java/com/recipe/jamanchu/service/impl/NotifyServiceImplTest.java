package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.dto.response.notify.Notify;
import com.recipe.jamanchu.repository.RecipeRepository;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;

@DisplayName("알림 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class NotifyServiceImplTest {

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private FluxSink<Notify> sink;

  @Mock
  private Map<Long, FluxSink<Notify>> subscribers;

  @InjectMocks
  private NotifyServiceImpl notifyService;

  @DisplayName("레시피 아이디를 통한 알림 구독 성공")
  @Test
  void subscribe() {
    // given
    Long recipeId = 1L;

    // when
    when(recipeRepository.existsById(recipeId)).thenReturn(true);

    // action
    notifyService.subscribe(recipeId, sink);

    // then
    verify(subscribers, atLeastOnce()).put(recipeId, sink);
    verify(sink, times(1)).onCancel(any());
  }

  @DisplayName("레시피 아이디를 통한 알림 구독 실패 : 레시피가 존재하지 않음")
  @Test
  void failSubscribe() {
    // given
    Long recipeId = 1L;

    // when
    when(recipeRepository.existsById(recipeId)).thenReturn(false);

    // action
    RecipeNotFoundException recipeNotFoundException = assertThrows(RecipeNotFoundException.class,
        () -> notifyService.subscribe(recipeId, sink));

    // then
    assertEquals("해당 레시피를 찾을 수 없습니다.", recipeNotFoundException.getMessage());
  }

  @DisplayName("레시피 구독 취소")
  @Test
  void successUnsubscribe() {
    //given
    Long recipeId = 1L;

    //when
    when(recipeRepository.existsById(recipeId)).thenReturn(true);

    doAnswer(invocation -> {
      ((Disposable) invocation.getArgument(0)).dispose();
      return null;
    }).when(sink).onCancel(any(Disposable.class));

    // action
    notifyService.subscribe(recipeId, sink);

    //then
    verify(sink, times(1)).onCancel(any());
    verify(sink).onCancel(any(Disposable.class));
    verify(subscribers, times(1)).remove(recipeId);
  }

  @DisplayName("알림 전송 성공")
  @Test
  void notifyUser() {
    // given
    Long userId = 1L;
    Notify notify = Notify.of("recipeName", "message", 5.0, "commentUser");
    // when
    doNothing().when(userAccessHandler).existsById(userId);
    when(subscribers.get(userId)).thenReturn(sink);

    // action
    notifyService.notifyUser(userId, notify);

    // then
    verify(sink, times(1)).next(notify);

  }

  @DisplayName("알림 전송 실패 : 사용자를 찾을 수 없음")
  @Test
  void failNotifyUser() {
    // given
    Long userId = 1L;
    Notify notify = Notify.of("recipeName", "message", 5.0, "commentUser");

    // when
    doThrow(UserNotFoundException.class).when(userAccessHandler).existsById(userId);

    // action

    // then
    assertThrows(
        UserNotFoundException.class,
        () -> notifyService.notifyUser(userId, notify)
    );
  }

  @DisplayName("알림 전송 실패 : 사용자가 구독하지 않음")
  @Test
  void failNotifyUser2() {
    // given
    Long userId = 1L;
    Notify notify = Notify.of("recipeName", "message", 5.0, "commentUser");

    // when
    doNothing().when(userAccessHandler).existsById(userId);
    when(subscribers.get(userId)).thenReturn(null);

    // action

    // then
    notifyService.notifyUser(userId, notify);
    assertNull(subscribers.get(userId));
    verify(sink, times(0)).next(notify);
  }
}