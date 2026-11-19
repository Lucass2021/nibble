package com.lucasdias.nibble.auth;

import com.lucasdias.nibble.user.User;
import com.lucasdias.nibble.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class MagicLinkService {

    private final UserRepository userRepository;
    private final LoginTokenRepository loginTokenRepository;
    private final JavaMailSender mailSender;

    public void requestLink(String email) {
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(new User(email)));

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String code = String.format("%06d", random.nextInt(1_000_000));
        Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);

        LoginToken loginToken = new LoginToken(user, sha256(token), sha256(code), expiresAt);
        loginTokenRepository.save(loginToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Nibble login link");
        message.setText("Tap to log in: nibble://login?token=" + token + "\n\nOr type this code: " + code);
        mailSender.send(message);
    }

    public User verifyToken(String token) {
        LoginToken loginToken = loginTokenRepository.findByTokenHash(sha256(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return consume(loginToken);
    }

    public User verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        LoginToken loginToken = loginTokenRepository.findByUserAndCodeHash(user, sha256(code))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return consume(loginToken);
    }

    private User consume(LoginToken loginToken){
        if (Instant.now().isAfter(loginToken.getExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (loginToken.getUsedAt() != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        loginToken.markUsed();
        loginTokenRepository.save(loginToken);

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
