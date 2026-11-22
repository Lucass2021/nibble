package com.lucasdias.nibble.topic.openrouter;

import tools.jackson.core.JacksonException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;
import com.lucasdias.nibble.topic.GeneratedTopic;
import com.lucasdias.nibble.topic.LlmClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.validation.Validator;
import java.time.Duration;
import java.util.List;


@Service
public class OpenRouterClient implements LlmClient {
    @Value("${openrouter.model}")
    private String model;

    private final RestClient restClient;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    public OpenRouterClient(@Value("${openrouter.base-url}") String baseUrl, JsonMapper jsonMapper, Validator validator) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(60));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();

        this.jsonMapper = jsonMapper;
        this.validator = validator;
    }

    @Override
    public GeneratedTopic generate(String topic, String language, String apiKey) {
        String systemPrompt = """
                You create short study content for a mobile learning app.
                The user sends a topic. Reply with JSON only, no markdown, no ```.
                Use exactly this format:
                {
                  "cards": [
                    {"title": "...", "content": "..."}
                  ],
                  "questions": [
                    {
                      "statement": "...",
                      "options": ["...", "...", "...", "..."],
                      "correctOption": 0,
                      "explanation": "..."
                    }
                  ]
                }
                Rules:
                - 5 cards, each content with at most 3 short sentences.
                - 3 questions, each with exactly 4 options.
                - correctOption is the index of the right option, starting at 0.
                - explanation says in one sentence why that option is right.
                - Write every title, content, statement, option and explanation in this language: %s.
                """.formatted(language);

        OpenRouterMessage systemMessage = new OpenRouterMessage("system", systemPrompt);
        OpenRouterMessage userMessage = new OpenRouterMessage("user", topic);
        OpenRouterRequest openRouterRequest = new OpenRouterRequest(
                model,
                List.of(systemMessage, userMessage),
                new ResponseFormat("json_object")
        );

        OpenRouterResponse response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(openRouterRequest)
                .retrieve()
                .body(OpenRouterResponse.class);

        String content = response.choices().get(0).message().content();

        GeneratedTopic generatedTopic;
        try {
            generatedTopic = jsonMapper.readValue(content, GeneratedTopic.class);
        } catch (JacksonException error) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "LLM returned invalid JSON");
        }

        if (!validator.validate(generatedTopic).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "LLM returned content in the wrong format");
        }

        return generatedTopic;
    }
}
