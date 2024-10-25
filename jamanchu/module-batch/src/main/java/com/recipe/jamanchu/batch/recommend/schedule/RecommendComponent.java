package com.recipe.jamanchu.batch.recommend.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RecommendComponent {

  @Bean
  public RecommendRecipeDifferencesMap recipeDifferences() {
    return new RecommendRecipeDifferencesMap();
  }

  @Bean
  public RecommendRecipeCountsMap recipeCounts() {
    return new RecommendRecipeCountsMap();
  }
}
