package com.recipe.jamanchu.batch.job;

import com.recipe.jamanchu.batch.recipe.RecipeDivide;
import com.recipe.jamanchu.batch.recipe.ScrapTenThousandRecipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CrawlDataJob {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final RecipeDivide recipeDivide;
  private final ScrapTenThousandRecipe scrapTenThousandRecipe;

  @Bean
  public Job crawlData(Step crawlDataStep) {
    log.info(">>> Crawl Data Job 실행");
    return new JobBuilder("crawlData", jobRepository)
        .start(crawlDataStep)
        .build();
  }

  @Bean
  public Step crawlDataStep() {
    log.info("crawlDataStep");
    return new StepBuilder("crawlData", jobRepository)
        .tasklet(crawlDataTasklet(), transactionManager)
        .build();
  }

  @Bean
  public Tasklet crawlDataTasklet() {
    return (contribution, chunkContext) -> {
      log.info(">>> 데이터 크롤링 로직 실행");
      scrapTenThousandRecipe.weeklyRecipeScrape();
      recipeDivide.weeklyRecipeDivide();
      return RepeatStatus.FINISHED;
    };
  }
}