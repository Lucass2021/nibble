package com.lucasdias.nibble.topic;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TopicController {
    private final LlmClient llmClient;

    @PostMapping("/topics")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(
            @RequestHeader("X-OpenRouter-Key") String apiKey,
            @Valid @RequestBody CreateTopicRequest request) {
        llmClient.generate(request.title(), apiKey);
    }

}