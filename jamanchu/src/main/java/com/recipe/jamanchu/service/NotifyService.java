package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.notify.Notify;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotifyService {

  // 사용자 기반 알림 설정
  SseEmitter subscribe(HttpServletRequest request);

  // 사용자에게 알림을 전달.
  void notifyUser(Long userId, Notify notify);

  // 특정 레시피에 대한 알림 설정 Toggle
  ResultResponse toggleSpecificRecipeCommentAlarm(HttpServletRequest request, Long recipeId);

  // 전체 레시피 알림 무시
  ResultResponse ignoreAllRecipeCommentAlarm(HttpServletRequest request);
}
