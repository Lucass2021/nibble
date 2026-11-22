package com.lucasdias.nibble.auth;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "magic-link")
public record MagicLinkProperties(
        @NotNull Duration expiration
) { }