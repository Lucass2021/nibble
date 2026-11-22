package com.lucasdias.nibble.auth.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MagicLinkRequest(
        @NotBlank @Email String email
) {}
