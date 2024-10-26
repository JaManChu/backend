package com.recipe.jamanchu.domain.component;

import com.recipe.jamanchu.domain.component.bean.IgnoreRecipeCommentAlarmMap;
import com.recipe.jamanchu.domain.component.bean.SseEmitterMap;
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
