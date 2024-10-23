package com.recipe.jamanchu.api.service.impl;

import static com.recipe.jamanchu.domain.model.type.TokenType.ACCESS;

import com.recipe.jamanchu.api.auth.jwt.JwtUtil;
import com.recipe.jamanchu.api.notify.IgnoreRecipeCommentAlarmMap;
import com.recipe.jamanchu.api.notify.SseEmitterMap;
import com.recipe.jamanchu.api.service.NotifyService;
import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.dto.response.notify.Notify;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotifyServiceImpl implements NotifyService {

  private final JwtUtil jwtUtil;
  private final UserAccessHandler userAccessHandler;
  private final SseEmitterMap subscribers;
  private final IgnoreRecipeCommentAlarmMap ignoreAlarmRecipeIds;
  private final RecipeRepository recipeRepository;

  // 레시피 아이디 구독
  @Override
  public SseEmitter subscribe(HttpServletRequest request) {

    Long userId = jwtUtil.getUserId(request.getHeader(ACCESS.getValue()));

    userAccessHandler.existsById(userId);

    SseEmitter sseEmitter = new SseEmitter(600_000_000L); //  1000분간 알림 설정 (임시)
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

    try {
      sseEmitter.send("Alarm Init Message");
    } catch (IOException e) {
      sseEmitter.complete();
      subscribers.remove(userId, sseEmitter);
    }

    return sseEmitter;
  }

  // 알림 전송
  @Override
  public void notifyUser(RecipeEntity recipe, Long userId, Notify notify) {
    userAccessHandler.existsById(userId);

    // 알림 무시한 레시피인 경우 알림 전송 X
    if (isIgnoreAlarm(userId, recipe.getId())) {
      return;
    }

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
   * 사용자의 알림 허용 여부 toggle
   * */
  @Override
  public ResultResponse toggleRecipeComment(HttpServletRequest request,
      Long recipeId) {

    Long userId = jwtUtil.getUserId(request.getHeader(ACCESS.getValue()));
    userAccessHandler.existsById(userId);
    if (ignoreAlarmRecipeIds.containsKey(userId)) {
      if (ignoreAlarmRecipeIds.containsIgnore(userId,recipeId)) {
        ignoreAlarmRecipeIds.removeIgnore(userId,recipeId);
      } else {
        ignoreAlarmRecipeIds.addIgnore(userId,recipeId);
      }
    } else {
      ignoreAlarmRecipeIds.put(userId, new HashSet<>(Set.of(recipeId)));
    }

    return getNotifyList(request);
  }

  /*
   * 사용자가 알림을 허용한 리스트 전체 조회
   * */
  @Override
  public ResultResponse getNotifyList(HttpServletRequest request) {

    Long userId = jwtUtil.getUserId(request.getHeader(ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    Optional<List<RecipeEntity>> allByUser = recipeRepository.findAllByUser(user);

    List<Long> list = List.of();
    if (allByUser.isPresent()) {
      list = new ArrayList<>(allByUser.get().stream()
          .map(RecipeEntity::getId)
          .toList());

      if (ignoreAlarmRecipeIds.containsKey(userId)) {
        Set<Long> ignoreRecipeIds = ignoreAlarmRecipeIds.get(userId);
        list.removeAll(ignoreRecipeIds);
      }
    }
    return ResultResponse.of(ResultCode.SUCCESS_GET_ALARM_LIST, list);
  }

  /*
   * 알림 무시 여부 확인
   * */
  private boolean isIgnoreAlarm(Long userId, Long recipeId) {
    return ignoreAlarmRecipeIds.containsKey(userId) && ignoreAlarmRecipeIds.get(userId)
        .contains(recipeId);
  }
}
