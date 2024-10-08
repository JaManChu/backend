package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.model.dto.response.notify.Notify;
import com.recipe.jamanchu.repository.RecipeRepository;
import com.recipe.jamanchu.service.NotifyService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

@RequiredArgsConstructor
@Service
public class NotifyServiceImpl implements NotifyService {

  private final RecipeRepository recipeRepository;
  private final UserAccessHandler userAccessHandler;
  private final Map<Long, FluxSink<Notify>> subscribers;

  // 레시피 아이디 구독
  @Override
  public void subscribe(Long recipeId, FluxSink<Notify> sink) {
    if(!recipeRepository.existsById(recipeId)){
      sink.error(new IllegalArgumentException("Recipe not found"));
      throw new RecipeNotFoundException();
    }else{
      subscribers.put(recipeId, sink);
      sink.onCancel(() -> subscribers.remove(recipeId));
    }
  }

  // 알림 전송
  @Override
  public void notifyUser(Long userId, Notify notify) {
    userAccessHandler.existsById(userId);
    FluxSink<Notify> sink = subscribers.get(userId);
    if (sink != null) {
      sink.next(notify);
    }
  }
}
