package com.recipe.jamanchu.batch.job;

import com.recipe.jamanchu.batch.recommend.RecommendCalculate;
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
public class RecommendCalculateJob {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final RecommendCalculate recommendCalculate;

  @Bean
  public Job calculate(Step updateDataStep) {
    log.info(">>> Calculate Job 실행");
    return new JobBuilder("calculate", jobRepository)
        .start(updateDataStep)
        .build();
  }

  @Bean
  public Step updateDataStep() {
    log.info("calculateStep");
    return new StepBuilder(" calculateStep", jobRepository)
        .tasklet(updateDataTasklet(), transactionManager)
        .build();
  }

  @Bean
  public Tasklet updateDataTasklet() {
    return (contribution, chunkContext) -> {
      log.info(">>> 추천 알고리즘 로직 실행");
      recommendCalculate.calculateAllRecommendations();
      return RepeatStatus.FINISHED;
    };
  }
}