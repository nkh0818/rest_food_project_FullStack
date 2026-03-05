package com.yonsai.rest_food_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestFoodProjectApplication {

	public static void main(String[] args) {
		io.github.cdimascio.dotenv.Dotenv.configure().systemProperties().load();
		SpringApplication.run(RestFoodProjectApplication.class, args);
	}

}
