package com.lucasdias.nibble.auth.domain;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("Invalid or expired login");
    }
}
