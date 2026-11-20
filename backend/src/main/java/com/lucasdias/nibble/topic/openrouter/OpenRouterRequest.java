package com.lucasdias.nibble.topic.openrouter;

import java.util.List;

public record OpenRouterRequest(
        String model,
        List<OpenRouterMessage> messages
) { }
