package com.yonsai.rest_food_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class RestFoodProjectApplication {

	public static void main(String[] args) {
		io.github.cdimascio.dotenv.Dotenv.configure()
				.ignoreIfMissing()
				.systemProperties()
				.load();

		SpringApplication.run(RestFoodProjectApplication.class, args);
	}

}
