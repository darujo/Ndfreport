package ru.darujo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringFrontApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringFrontApplication.class, args);
	}

}
