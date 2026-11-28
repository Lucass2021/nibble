package com.lucasdias.nibble.auth.infrastructure.email;

import com.lucasdias.nibble.auth.domain.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {
    private final JavaMailSender mailSender;

    @Override
    public void sendLoginEmail(String recipient, String token, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Your Nibble login link");
        message.setText("Tap to log in: nibble://login?token=" + token + "\n\nOr type this code: " + code);
        mailSender.send(message);
    }
}
