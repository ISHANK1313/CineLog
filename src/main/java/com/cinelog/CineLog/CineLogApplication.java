package com.cinelog.CineLog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:secrets.properties")
public class CineLogApplication {

	public static void main(String[] args) {

		SpringApplication.run(CineLogApplication.class, args);
	}

}
