package com.lucasdias.nibble.topic.infrastructure.web;

import com.lucasdias.nibble.topic.domain.GeneratedTopic;
import com.lucasdias.nibble.topic.domain.LlmClient;
import com.lucasdias.nibble.topic.infrastructure.web.dto.CreateTopicRequest;
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
    public GeneratedTopic create(
            @RequestHeader("X-OpenRouter-Key") String apiKey,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language,
            @Valid @RequestBody CreateTopicRequest request) {
        return llmClient.generate(request.title(), language, apiKey);
    }
}
