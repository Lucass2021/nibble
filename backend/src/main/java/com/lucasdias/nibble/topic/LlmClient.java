package com.lucasdias.nibble.topic;

public interface LlmClient {
    GeneratedTopic generate(String topic, String language, String apiKey);
}
