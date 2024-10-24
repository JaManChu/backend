package com.recipe.jamanchu.batch.job;

import com.recipe.jamanchu.domain.component.UserAccessHandler;
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
public class DeleteUserInfoJob {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final UserAccessHandler userAccessHandler;


  @Bean
  public Job deleteUserInfo(Step deleteUserInfoStep) {
    log.info(">>> Delete User Job 실행");
    return new JobBuilder("deleteUserInfo", jobRepository)
        .start(deleteUserInfoStep)
        .build();
  }

  @Bean
  public Step deleteUserInfoStep() {
    log.info("deleteUserInfoStep");
    return new StepBuilder("deleteUserInfoStep", jobRepository)
        .tasklet(deleteUserTasklet(), transactionManager)
        .build();
  }

  @Bean
  public Tasklet deleteUserTasklet() {
    return (contribution, chunkContext) -> {
      log.info(">>> 탈퇴한 회원 정보 삭제 로직 실행");
      userAccessHandler.deleteAllUserData();
      return RepeatStatus.FINISHED;
    };
  }
}