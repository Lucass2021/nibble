package com.lucasdias.nibble.auth.application;

import com.lucasdias.nibble.auth.domain.*;
import com.lucasdias.nibble.user.domain.User;
import com.lucasdias.nibble.user.domain.UserRepository;
import java.security.SecureRandom;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MagicLinkService {
    private static final int CODE_BOUND = 1_000_000;
    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_LINK_REQUESTS = 3;

    private final SecureRandom random = new SecureRandom();

    private final UserRepository userRepository;
    private final LoginTokenRepository loginTokenRepository;
    private final MagicLinkProperties properties;
    private final EmailSender emailSender;

    public void requestLink(String email) {
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(new User(email)));

        long activeTokenCount = loginTokenRepository.countByUserAndExpiresAtAfter(user, Instant.now());

        if (activeTokenCount >= MAX_LINK_REQUESTS) {
            throw new TooManyLoginRequestsException();
        }

        String token = SecureTokens.generate();

        String code = String.format("%06d", random.nextInt(CODE_BOUND));
        Instant expiresAt = Instant.now().plus(properties.expiration());

        LoginToken loginToken = new LoginToken(user, SecureTokens.sha256(token), SecureTokens.sha256(code), expiresAt);
        loginTokenRepository.save(loginToken);

        emailSender.sendLoginEmail(email, token, code);
    }

    @Transactional
    public User verifyToken(String token) {
        LoginToken loginToken = loginTokenRepository
                .findByTokenHash(SecureTokens.sha256(token))
                .orElseThrow(() -> new InvalidLoginException());

        return consume(loginToken);
    }

    @Transactional(noRollbackFor = InvalidLoginException.class)
    public User verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidLoginException());

        LoginToken loginToken =
                loginTokenRepository.findFirstByUserOrderByIdDesc(user).orElseThrow(() -> new InvalidLoginException());

        if (loginToken.getAttempts() >= MAX_ATTEMPTS) {
            throw new InvalidLoginException();
        }

        if (!SecureTokens.sha256(code).equals(loginToken.getCodeHash())) {
            loginToken.registerFailedAttempt();
            throw new InvalidLoginException();
        }

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
}
