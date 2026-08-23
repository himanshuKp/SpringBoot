package com.himanshu.transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.himanshu.transactional.entities.User;
import com.himanshu.transactional.repository.UserRepository;

@SpringBootApplication
public class TransactionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionalApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			userRepository.save(new User("himanshu", "himanshu@one.com", true));
			userRepository.save(new User("himanshu2", "himanshu2@one.com", true));
		};
	}

}
