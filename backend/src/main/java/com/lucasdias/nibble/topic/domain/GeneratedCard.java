package com.lucasdias.nibble.topic.domain;

import jakarta.validation.constraints.NotBlank;

public record GeneratedCard(
        @NotBlank String title, @NotBlank String content) {}
