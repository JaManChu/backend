package com.recipe.jamanchu.batch.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobRunner implements ApplicationRunner {

  private final JobLauncher jobLauncher;
  private final Job deleteUserInfo;
  private final Job calculate;
  private final Job crawlData;
  private final JobExecutionHolder jobExecutionHolder;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (args.containsOption("job.name")) {
      String jobName = args.getOptionValues("job.name").get(0);
      log.info("Job name >>> {}", jobName);

      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("time", System.currentTimeMillis())
          .toJobParameters();

      JobExecution jobExecution = null;

      switch (jobName) {
        case "deleteUserInfo" -> {
          log.info(">>> Delete user info");
          jobExecution = jobLauncher.run(deleteUserInfo, jobParameters);
        }
        case "calculate" -> {
          log.info(">>> Calculate");
          jobExecution = jobLauncher.run(calculate, jobParameters);
        }
        case "crawlData" -> {
          log.info(">>> Crawl data");
          jobExecution = jobLauncher.run(crawlData, jobParameters);
        }
        case null, default -> log.warn("Unknown job name: {}", jobName);
      }

      if (jobExecution != null) {
        log.info("작업 명 : '{}', 작업 결과 : {}", jobName, jobExecution.getStatus());
        jobExecutionHolder.setJobExecution(jobExecution);
      }
    } else {
      log.warn("No job name specified");
    }
  }
}
