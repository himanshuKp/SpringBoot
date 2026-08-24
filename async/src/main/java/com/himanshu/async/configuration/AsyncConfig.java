package com.himanshu.async.configuration;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// @Configuration indicates that the class can be used by the Spring IoC container as a source of bean definitions.
@Configuration
public class AsyncConfig {

    @Bean("taskExecutor") // Define a bean named "taskExecutor" for managing asynchronous tasks
    public Executor taskExecutor() {
        // Create a ThreadPoolTaskExecutor to manage asynchronous tasks
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Configure the thread pool executor with core pool size, max pool size, queue capacity, and thread name prefix
        executor.setCorePoolSize(5); // Set the core pool size
        executor.setMaxPoolSize(10); // Set the maximum pool size
        executor.setQueueCapacity(25); // Set the queue capacity
        executor.setThreadNamePrefix("Async-"); // Set the thread name prefix
        executor.initialize(); // Initialize the executor
        return executor; // Return the configured executor
    }
}
