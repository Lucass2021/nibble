package com.lucasdias.nibble.topic.domain;

public class LlmResponseException extends RuntimeException {
    public LlmResponseException(String message) {
        super(message);
    }
}