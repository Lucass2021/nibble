package com.lucasdias.nibble.auth.application;

import com.lucasdias.nibble.auth.domain.RefreshToken;
import com.lucasdias.nibble.auth.domain.RefreshTokenRepository;
import com.lucasdias.nibble.user.domain.User;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProperties refreshTokenProperties;

    public String issue(User user) {
        String token = SecureTokens.generate();

        Instant expiresAt = Instant.now().plus(refreshTokenProperties.expiration());

        RefreshToken refreshToken = new RefreshToken(user, SecureTokens.sha256(token), expiresAt);
        refreshTokenRepository.save(refreshToken);

        return token;
    }
}
