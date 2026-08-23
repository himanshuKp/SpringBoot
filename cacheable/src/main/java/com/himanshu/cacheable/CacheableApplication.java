package com.himanshu.cacheable;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.himanshu.cacheable.entities.User;
import com.himanshu.cacheable.repository.UserRepository;

@SpringBootApplication
@EnableCaching
public class CacheableApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheableApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(UserRepository userRepository) {
		return args -> {
			userRepository.save(new User("John Doe", "john.doe@example.com", true));
			userRepository.save(new User("Jane Smith", "jane.smith@example.com", false));
		};
	}

}
