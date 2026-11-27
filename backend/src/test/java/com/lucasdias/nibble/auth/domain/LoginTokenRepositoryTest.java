package com.lucasdias.nibble.auth.domain;

import com.lucasdias.nibble.TestcontainersConfiguration;
import com.lucasdias.nibble.user.domain.User;
import com.lucasdias.nibble.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@Testcontainers
class LoginTokenRepositoryTest {
    private static final String EMAIL = "lucas@email.com";
    private static final String CODE_HASH = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";


    @Autowired
    private LoginTokenRepository loginTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void countByUserAndExpiresAtAfter_ignoresExpiredTokens() {
        User user = userRepository.save(new User(EMAIL));

        loginTokenRepository.save(
                new LoginToken(user, "token-hash-1", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)))
        );

        loginTokenRepository.save(
                new LoginToken(user, "token-hash-2", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)))
        );

        loginTokenRepository.save(
                new LoginToken(user, "token-hash-3", CODE_HASH, Instant.now().minus(Duration.ofMinutes(15)))
        );

        assertEquals(2, loginTokenRepository.countByUserAndExpiresAtAfter(user, Instant.now()));
    }

    @Test
    void findFirstByUserOrderByIdDesc_returnsNewestToken() {
        User user = userRepository.save(new User(EMAIL));

        loginTokenRepository.save(
                new LoginToken(user, "token-hash-1", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)))
        );

        loginTokenRepository.save(
                new LoginToken(user, "token-hash-2", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)))
        );

        LoginToken newest = loginTokenRepository.findFirstByUserOrderByIdDesc(user).orElseThrow();

        assertEquals("token-hash-2", newest.getTokenHash());
    }
}