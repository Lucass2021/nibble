package com.lucasdias.nibble.auth.domain;

public interface EmailSender {
    void sendLoginEmail(String recipient, String token, String code);
}
