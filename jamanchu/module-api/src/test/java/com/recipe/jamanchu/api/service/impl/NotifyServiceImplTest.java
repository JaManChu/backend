package com.recipe.jamanchu.api.service.impl;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.api.auth.jwt.JwtUtil;
import com.recipe.jamanchu.api.component.UserAccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
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
  private SseEmitter sseEmitter;

  @Mock
  private Map<Long, SseEmitter> subscribers;

  @InjectMocks
  private NotifyServiceImpl notifyService;

  @DisplayName("Access-Token을 통한 알림 구독 성공")
  @Test
  void subscribe() throws IOException {
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

//  @DisplayName("레시피 구독 취소")
//  @Test
//  void successUnsubscribe() {
//    //given
//    Long recipeId = 1L;
//
//    //when
//    when(recipeRepository.existsById(recipeId)).thenReturn(true);
//
//    doAnswer(invocation -> {
//      ((Disposable) invocation.getArgument(0)).dispose();
//      return null;
//    }).when(sink).onCancel(any(Disposable.class));
//
//    // action
//    notifyService.subscribe(recipeId);
//
//    //then
//    verify(sink, times(1)).onCancel(any());
//    verify(sink).onCancel(any(Disposable.class));
//    verify(subscribers, times(1)).remove(recipeId);
//  }
//
//  @DisplayName("알림 전송 성공")
//  @Test
//  void notifyUser() {
//    // given
//    Long userId = 1L;
//    Notify notify = Notify.of("recipeName", "message", 5.0, "commentUser");
//    // when
//    doNothing().when(userAccessHandler).existsById(userId);
//    when(subscribers.get(userId)).thenReturn(sink);
//
//    // action
//    notifyService.notifyUser(userId, notify);
//
//    // then
//    verify(sink, times(1)).next(notify);
//
//  }
//
//  @DisplayName("알림 전송 실패 : 사용자를 찾을 수 없음")
//  @Test
//  void failNotifyUser() {
//    // given
//    Long userId = 1L;
//    Notify notify = Notify.of("recipeName", "message", 5.0, "commentUser");
//
//    // when
//    doThrow(UserNotFoundException.class).when(userAccessHandler).existsById(userId);
//
//    // action
//
//    // then
//    assertThrows(
//        UserNotFoundException.class,
//        () -> notifyService.notifyUser(userId, notify)
//    );
//  }
//
//  @DisplayName("알림 전송 실패 : 사용자가 구독하지 않음")
//  @Test
//  void failNotifyUser2() {
//    // given
//    Long userId = 1L;
//    Notify notify = Notify.of("recipeName", "message", 5.0, "commentUser");
//
//    // when
//    doNothing().when(userAccessHandler).existsById(userId);
//    when(subscribers.get(userId)).thenReturn(null);
//
//    // action
//
//    // then
//    notifyService.notifyUser(userId, notify);
//    assertNull(subscribers.get(userId));
//    verify(sink, times(0)).next(notify);
//  }
}