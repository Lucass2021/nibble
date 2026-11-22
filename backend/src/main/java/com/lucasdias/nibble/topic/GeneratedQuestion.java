package com.lucasdias.nibble.topic;

import jakarta.validation.constraints.*;

import java.util.List;

public record GeneratedQuestion (
        @NotBlank String statement,
        @NotNull @Size(min = 4, max = 4) List<String> options,
        @Min(0) @Max(3) int correctOption,
        @NotBlank String explanation
) { }