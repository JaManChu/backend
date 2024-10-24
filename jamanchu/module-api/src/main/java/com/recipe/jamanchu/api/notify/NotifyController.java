package com.recipe.jamanchu.api.notify;

import com.recipe.jamanchu.api.service.NotifyService;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class NotifyController {

  private final NotifyService notifyService;

  // 토큰 기반 알림 전체 연결
  @GetMapping(value = "/notify", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter getNotifications(HttpServletRequest request) {
    return notifyService.subscribe(request);
  }

  // 특정 레시피에 대한 알림 toggle
  @GetMapping("/notify/toggle/{recipeId}")
  public ResponseEntity<ResultResponse> toggleRecipeCommentAlarm(HttpServletRequest request,
      @PathVariable("recipeId") Long recipeId) {
    return ResponseEntity.ok(notifyService.toggleRecipeComment(request, recipeId));
  }

  @GetMapping("/notify/list")
  public ResponseEntity<ResultResponse> getNotifyList(HttpServletRequest request) {
    return ResponseEntity.ok(notifyService.getNotifyList(request));
  }
}
