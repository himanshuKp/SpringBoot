package com.himanshu.home;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.himanshu.home.records.MailProperties;
import com.himanshu.home.services.EmailService;

@SpringBootApplication
@EnableConfigurationProperties(MailProperties.class)
public class HomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(EmailService emailService) {
		return args -> {
			emailService.printConnectionDetails();
		};
	}

}
