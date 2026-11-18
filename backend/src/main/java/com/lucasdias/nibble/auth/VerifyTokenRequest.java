package com.lucasdias.nibble.auth;

import jakarta.validation.constraints.NotBlank;

public record VerifyTokenRequest(
        @NotBlank String token
) { }
