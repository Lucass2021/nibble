package com.lucasdias.nibble.topic;

import jakarta.validation.constraints.NotBlank;

public record CreateTopicRequest(
        @NotBlank String title
) {}