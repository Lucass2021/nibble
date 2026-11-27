package com.lucasdias.nibble.topic.infrastructure.llm.openrouter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "openrouter")
public record OpenRouterProperties(
        @NotBlank String baseUrl,
        @NotBlank String model,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout) {}
