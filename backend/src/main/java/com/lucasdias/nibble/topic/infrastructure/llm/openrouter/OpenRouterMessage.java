package com.lucasdias.nibble.topic.infrastructure.llm.openrouter;

public record OpenRouterMessage(
        String role,
        String content
) { }
