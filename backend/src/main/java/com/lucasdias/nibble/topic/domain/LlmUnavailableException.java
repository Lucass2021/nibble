package com.lucasdias.nibble.topic.domain;

public class LlmUnavailableException extends RuntimeException {
    public LlmUnavailableException(String message) {
        super(message);
    }
}