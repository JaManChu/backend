package com.recipe.jamanchu.batch.batch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class JobExecutionHolder {

  private JobExecution jobExecution;

}
