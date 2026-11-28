package com.lucasdias.nibble.auth.application;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "refresh-token")
public record RefreshTokenProperties(@NotNull Duration expiration) {}
