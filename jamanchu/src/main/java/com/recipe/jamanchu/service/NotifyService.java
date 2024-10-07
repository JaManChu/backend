package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.response.notify.Notify;
import reactor.core.publisher.FluxSink;

public interface NotifyService {

  void subscribe(Long recipeId, FluxSink<Notify> sink);

  void notifyUser(Long userId, Notify notify);
}
