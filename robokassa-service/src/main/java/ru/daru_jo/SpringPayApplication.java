package ru.daru_jo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringPayApplication.class, args);
	}

}
