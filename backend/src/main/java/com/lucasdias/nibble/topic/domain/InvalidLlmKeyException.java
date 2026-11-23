package com.lucasdias.nibble.topic.domain;

public class InvalidLlmKeyException extends RuntimeException {
    public InvalidLlmKeyException(String message) {
        super(message);
    }
}
