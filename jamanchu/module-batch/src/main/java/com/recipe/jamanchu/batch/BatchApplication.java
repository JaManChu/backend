package com.recipe.jamanchu.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.recipe.jamanchu.domain.repository")
@EntityScan(basePackages = "com.recipe.jamanchu.domain")
@ComponentScan(basePackages = "com.recipe.jamanchu")
@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		int exit = SpringApplication.exit(SpringApplication.run(BatchApplication.class, args));
		log.info("exit = {}", exit);
		System.exit(exit);
	}

}
