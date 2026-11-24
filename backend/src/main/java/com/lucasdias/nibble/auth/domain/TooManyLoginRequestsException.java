package com.lucasdias.nibble.auth.domain;

public class TooManyLoginRequestsException extends RuntimeException {
    public TooManyLoginRequestsException() {
        super("Too many login requests, try again later");
    }
}