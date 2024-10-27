package com.recipe.jamanchu.domain.component;

import com.recipe.jamanchu.domain.component.bean.StatisticsSet;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StatisticsComponent {

  @Bean
  public StatisticsSet statisticsSet() {
    return new StatisticsSet();
  }
}
