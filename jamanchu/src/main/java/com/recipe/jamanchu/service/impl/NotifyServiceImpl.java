package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.notify.Notify;
import com.recipe.jamanchu.service.NotifyService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class NotifyServiceImpl implements NotifyService {

  private final JwtUtil jwtUtil;
  private final UserAccessHandler userAccessHandler;
  private final Map<Long, SseEmitter> subscribers;

  // 레시피 아이디 구독
  @Override
  public SseEmitter subscribe(HttpServletRequest request) {

    Long userId = jwtUtil.getUserId(request.getHeader("Access-Token"));

    userAccessHandler.existsById(userId);

    SseEmitter sseEmitter = new SseEmitter();
    subscribers.put(userId, sseEmitter);

    // SSE 연결 해제 시
    sseEmitter.onCompletion(() -> subscribers.remove(userId, sseEmitter));

    // SSE 연결 시간 초과 시
    sseEmitter.onTimeout(() -> {
      sseEmitter.complete();
      subscribers.remove(userId, sseEmitter);
    });

    // SSE 에러 발생 시
    sseEmitter.onError(e -> {
      sseEmitter.complete();
      subscribers.remove(userId, sseEmitter);
    });

    try{
      sseEmitter.send("Alarm Init Message");
    }catch (IOException e){
      sseEmitter.complete();
      subscribers.remove(userId, sseEmitter);
    }


    return sseEmitter;
  }

  // 알림 전송
  @Override
  public void notifyUser(Long userId, Notify notify) {
    userAccessHandler.existsById(userId);

    SseEmitter userSseEmitter = subscribers.get(userId);
    if (userSseEmitter != null) {
      try {
        userSseEmitter.send(
            SseEmitter.event()
              .name("댓글 알림!")
              .data(notify)
        );
      } catch (Exception e) {
        userSseEmitter.completeWithError(e);
        subscribers.remove(userId);
      }
    }
  }

  /*
   * 특정 레시피 알림 Toggle
   */
  @Override
  public ResultResponse toggleSpecificRecipeCommentAlarm(HttpServletRequest request,
      Long recipeId) {
    return null;
  }

  /*
   * 전체 레시피 알림 무시
   */
  @Override
  public ResultResponse ignoreAllRecipeCommentAlarm(HttpServletRequest request) {
    return null;
  }
}
