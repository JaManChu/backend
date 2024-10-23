package com.recipe.jamanchu.api.service;

import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.dto.response.notify.Notify;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotifyService {

  // 사용자 기반 알림 설정
  SseEmitter subscribe(HttpServletRequest request);

  // 사용자에게 알림을 전달.
  void notifyUser(RecipeEntity recipe, Long userId, Notify notify);

  // 특정 레시피에 대한 알림 설정 Toggle
  ResultResponse toggleRecipeComment(HttpServletRequest request, Long recipeId);

  // 해당 유저의 알림 리스트 반환
  ResultResponse getNotifyList(HttpServletRequest request);
}
