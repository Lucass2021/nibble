package com.lucasdias.nibble.topic.openrouter;

import java.util.List;

public record OpenRouterResponse(
        List<OpenRouterChoice> choices
) { }
