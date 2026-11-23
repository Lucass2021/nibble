package com.lucasdias.nibble.topic.infrastructure.llm.openrouter;

import com.lucasdias.nibble.topic.domain.*;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.net.http.HttpClient;
import java.util.List;

@Service
public class OpenRouterClient implements LlmClient {
    private final OpenRouterProperties properties;

    private final RestClient restClient;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    public OpenRouterClient(OpenRouterProperties properties, JsonMapper jsonMapper, Validator validator) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(properties.readTimeout());

        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();

        this.properties = properties;
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
                - %d cards, each content with at most 3 short sentences.
                - %d questions, each with exactly %d options.
                - correctOption is the index of the right option, starting at 0.
                - explanation says in one sentence why that option is right.
                - Write every title, content, statement, option and explanation in this language: %s.
                """
                .formatted(
                        GeneratedTopic.CARD_COUNT,
                        GeneratedTopic.QUESTION_COUNT,
                        GeneratedQuestion.OPTION_COUNT,
                        language
                );

        OpenRouterMessage systemMessage = new OpenRouterMessage("system", systemPrompt);
        OpenRouterMessage userMessage = new OpenRouterMessage("user", topic);
        OpenRouterRequest openRouterRequest = new OpenRouterRequest(
                properties.model(),
                List.of(systemMessage, userMessage),
                new ResponseFormat("json_object")
        );

        OpenRouterResponse response;

        try {
            response = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(openRouterRequest)
                    .retrieve()
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.UNAUTHORIZED), (request, errorResponse) -> {
                        throw new InvalidLlmKeyException("Invalid LLM API key");
                    })
                    .body(OpenRouterResponse.class);
        } catch (ResourceAccessException error) {
            throw new LlmUnavailableException("LLM provider did not respond");
        }

        if (!validator.validate(response).isEmpty()) {
            throw new LlmResponseException("LLM returned no choices");
        }

        String content = response.choices().get(0).message().content();

        GeneratedTopic generatedTopic;
        try {
            generatedTopic = jsonMapper.readValue(content, GeneratedTopic.class);
        } catch (JacksonException error) {
            throw new LlmResponseException("LLM returned invalid JSON");
        }

        if (!validator.validate(generatedTopic).isEmpty()) {
            throw new LlmResponseException("LLM returned content in the wrong format");
        }

        return generatedTopic;
    }
}
