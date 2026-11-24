package com.lucasdias.nibble.auth.application;

import com.lucasdias.nibble.auth.domain.InvalidLoginException;
import com.lucasdias.nibble.auth.domain.LoginToken;
import com.lucasdias.nibble.auth.domain.LoginTokenRepository;
import com.lucasdias.nibble.user.domain.User;
import com.lucasdias.nibble.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class MagicLinkService {
    private static final int TOKEN_BYTES = 32;
    private static final int CODE_BOUND = 1_000_000;

    private final SecureRandom random = new SecureRandom();

    private final UserRepository userRepository;
    private final LoginTokenRepository loginTokenRepository;
    private final JavaMailSender mailSender;
    private final MagicLinkProperties properties;

    public void requestLink(String email) {
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(new User(email)));

        byte[] bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);

        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String code = String.format("%06d", random.nextInt(CODE_BOUND));
        Instant expiresAt = Instant.now().plus(properties.expiration());

        LoginToken loginToken = new LoginToken(user, sha256(token), sha256(code), expiresAt);
        loginTokenRepository.save(loginToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Nibble login link");
        message.setText("Tap to log in: nibble://login?token=" + token + "\n\nOr type this code: " + code);
        mailSender.send(message);
    }

    @Transactional
    public User verifyToken(String token) {
        LoginToken loginToken = loginTokenRepository.findByTokenHash(sha256(token))
                .orElseThrow(() -> new InvalidLoginException());

        return consume(loginToken);
    }

    @Transactional
    public User verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidLoginException());

        LoginToken loginToken = loginTokenRepository.findByUserAndCodeHash(user, sha256(code))
                .orElseThrow(() -> new InvalidLoginException());

        return consume(loginToken);
    }

    private User consume(LoginToken loginToken) {
        if (Instant.now().isAfter(loginToken.getExpiresAt())) {
            throw new InvalidLoginException();
        }

        if (loginToken.getUsedAt() != null) {
            throw new InvalidLoginException();
        }

        loginToken.markUsed();

        return loginToken.getUser();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException(error);
        }
    }
}
