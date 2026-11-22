package com.lucasdias.nibble.topic.domain;

public interface LlmClient {
    GeneratedTopic generate(String topic, String language, String apiKey);
}
