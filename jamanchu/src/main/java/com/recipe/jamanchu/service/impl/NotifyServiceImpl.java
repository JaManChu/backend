package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.model.dto.response.notify.Notify;
import com.recipe.jamanchu.service.NotifyService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

@RequiredArgsConstructor
@Service
public class NotifyServiceImpl implements NotifyService {

  private final Map<Long, FluxSink<Notify>> subscribers;

  @Override
  public void subscribe(Long recipeId, FluxSink<Notify> sink) {
    subscribers.put(recipeId, sink);
    sink.onCancel(() -> subscribers.remove(recipeId));
  }

  @Override
  public void notifyUser(Long userId, Notify notify) {
    FluxSink<Notify> sink = subscribers.get(userId);
    if (sink != null) {
      sink.next(notify);
    }
  }
}
