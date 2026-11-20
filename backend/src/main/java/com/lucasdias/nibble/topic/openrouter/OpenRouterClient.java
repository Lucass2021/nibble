package com.lucasdias.nibble.topic.openrouter;

import com.lucasdias.nibble.topic.GeneratedTopic;
import com.lucasdias.nibble.topic.LlmClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;


@Service
public class OpenRouterClient implements LlmClient {
    @Value("${openrouter.base-url}")
    private String baseUrl;

    @Value("${openrouter.model}")
    private String model;

    @Override
    public GeneratedTopic generate(String topic, String apiKey) {
        OpenRouterMessage systemMessage = new OpenRouterMessage( "system", "You generate study content.");
        OpenRouterMessage userMessage = new OpenRouterMessage( "user", topic);
        OpenRouterRequest modelAndMessage = new OpenRouterRequest(model, List.of(systemMessage, userMessage));

        RestClient restClient = RestClient.create(baseUrl);

        OpenRouterResponse response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(modelAndMessage)
                .retrieve()
                .body(OpenRouterResponse.class);

        String content = response.choices().get(0).message().content();

        System.out.println(content);

        return null;
    }
}
