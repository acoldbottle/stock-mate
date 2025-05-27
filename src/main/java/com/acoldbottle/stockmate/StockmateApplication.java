package com.acoldbottle.stockmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class StockmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockmateApplication.class, args);
	}

}
