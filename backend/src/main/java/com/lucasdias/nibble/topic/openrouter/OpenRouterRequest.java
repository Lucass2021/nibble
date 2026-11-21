package com.lucasdias.nibble.topic.openrouter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenRouterRequest(
        String model,
        List<OpenRouterMessage> messages,
        @JsonProperty("response_format") ResponseFormat responseFormat
) { }
