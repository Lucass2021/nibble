package com.lucasdias.nibble.topic.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTopicRequest(@NotBlank String title) {}
