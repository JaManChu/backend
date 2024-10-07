package com.recipe.jamanchu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JamanchuApplication {

	public static void main(String[] args) {
		SpringApplication.run(JamanchuApplication.class, args);
	}

}
