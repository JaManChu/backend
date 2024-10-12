package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.response.notify.Notify;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotifyService {

  SseEmitter subscribe(HttpServletRequest request);

  void notifyUser(Long userId, Notify notify);
}
