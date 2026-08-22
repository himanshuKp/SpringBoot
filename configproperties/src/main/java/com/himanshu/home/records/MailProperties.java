package com.himanshu.home.records;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "app.mail")
public record MailProperties(
    @NotBlank(message="Host cannot be blank")
    String host,

    @Min(value=1, message="Port must be greater than 0")
    @Max(value=65535, message="Port must be less than or equal to 65535")
    int port
) {}