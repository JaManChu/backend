package com.recipe.jamanchu.api.notify;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class NotifyComponent {

  @Bean
  public SseEmitterMap submissionPublisher() {
    return new SseEmitterMap();
  }

  @Bean
  public IgnoreRecipeCommentAlarmMap ignoreRecipeCommentAlarmMap() {
    return new IgnoreRecipeCommentAlarmMap();
  }
}
