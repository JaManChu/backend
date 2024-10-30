package com.recipe.jamanchu.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CrawlDataJob {

  private final JobRepository jobRepository;
  private final Step scrapData;

  @Bean
  public Job crawlData() {
    return new JobBuilder("crawlData", jobRepository)
        .start(scrapData)
        .build();
  }

}