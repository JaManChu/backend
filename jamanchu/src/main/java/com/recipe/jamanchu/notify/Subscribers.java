package com.recipe.jamanchu.notify;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class Subscribers {

  @Bean
  public Map<Long, SseEmitter> submissionPublisher(){
    return new ConcurrentHashMap<>();
  }

}
