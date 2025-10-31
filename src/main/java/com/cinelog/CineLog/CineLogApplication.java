package com.cinelog.CineLog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:secrets.properties", ignoreResourceNotFound = true)
public class CineLogApplication {

	public static void main(String[] args) {

		SpringApplication.run(CineLogApplication.class, args);
	}

}
