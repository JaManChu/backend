package com.recipe.jamanchu.batch.recommend.schedule;

import com.recipe.jamanchu.batch.recommend.RecommendCalculate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RecommendScheduler {

  private final RecommendCalculate recommendCalculate;

  @Scheduled(cron = "0 0 7 * * ?")
  public void calculateRecommend1() {
    recommendCalculate.calculateAllRecommendations();
  }

  @Scheduled(cron = "0 0 11 * * ?")
  public void calculateRecommend2() {
    recommendCalculate.calculateAllRecommendations();
  }

  @Scheduled(cron = "0 0 17 * * ?")
  public void calculateRecommend3() {
    recommendCalculate.calculateAllRecommendations();
  }

}
