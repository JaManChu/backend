package com.recipe.jamanchu.notify;

import com.recipe.jamanchu.service.impl.NotifyServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class NotifyController {

  private final NotifyServiceImpl notifyService;

  @GetMapping(value = "/notify", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter getNotifications(HttpServletRequest request) {
    return notifyService.subscribe(request);
  }
}
