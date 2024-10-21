package com.recipe.jamanchu.api.schedule;

import com.recipe.jamanchu.api.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RecommendScheduler {

  private final RecipeService recipeService;

  @Scheduled(cron = "0 0 7 * * ?")
  public void calculateRecommend1(){
    recipeService.calculateAllRecommendations();
  }

  @Scheduled(cron = "0 0 11 * * ?")
  public void calculateRecommend2(){
    recipeService.calculateAllRecommendations();
  }

  @Scheduled(cron = "0 0 17 * * ?")
  public void calculateRecommend3(){
    recipeService.calculateAllRecommendations();
  }

}
