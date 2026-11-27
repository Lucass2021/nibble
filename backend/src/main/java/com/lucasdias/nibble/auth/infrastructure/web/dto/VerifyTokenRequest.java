package com.lucasdias.nibble.auth.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyTokenRequest(@NotBlank String token) {}
