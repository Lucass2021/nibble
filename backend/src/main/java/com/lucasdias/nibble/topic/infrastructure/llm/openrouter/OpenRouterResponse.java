package com.lucasdias.nibble.topic.infrastructure.llm.openrouter;

import java.util.List;

public record OpenRouterResponse(
        List<OpenRouterChoice> choices
) { }
