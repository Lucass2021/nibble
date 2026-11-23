package com.lucasdias.nibble.topic.infrastructure.llm.openrouter;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OpenRouterResponse(
        @NotEmpty
        List<OpenRouterChoice> choices
) {
}
