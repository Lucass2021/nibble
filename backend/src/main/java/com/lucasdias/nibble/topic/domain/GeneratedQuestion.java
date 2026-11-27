package com.lucasdias.nibble.topic.domain;

import jakarta.validation.constraints.*;
import java.util.List;

public record GeneratedQuestion(
        @NotBlank String statement,

        @NotNull @Size(min = OPTION_COUNT, max = OPTION_COUNT)
        List<String> options,

        @Min(0) @Max(OPTION_COUNT - 1) int correctOption,
        @NotBlank String explanation) {
    public static final int OPTION_COUNT = 4;
}
