package com.lucasdias.nibble.auth.application;

import com.lucasdias.nibble.auth.domain.InvalidLoginException;
import com.lucasdias.nibble.auth.domain.LoginToken;
import com.lucasdias.nibble.auth.domain.LoginTokenRepository;
import com.lucasdias.nibble.auth.domain.TooManyLoginRequestsException;
import com.lucasdias.nibble.user.domain.User;
import com.lucasdias.nibble.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MagicLinkServiceTest {
    private static final String EMAIL = "lucas@email.com";
    private static final String CODE = "123456";
    private static final String CODE_HASH = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginTokenRepository loginTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MagicLinkProperties properties;

    @InjectMocks
    private MagicLinkService magicLinkService;

    @Test
    void requestLink_throwsWhenThreeLinksAreActive() {
        User user = new User("lucas@email.com");
        when(userRepository.findByEmail("lucas@email.com")).thenReturn(Optional.of(user));
        when(loginTokenRepository.countByUserAndExpiresAtAfter(eq(user), any(Instant.class))).thenReturn(3L);

        assertThrows(TooManyLoginRequestsException.class, () -> magicLinkService.requestLink("lucas@email.com"));

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void verifyCode_throwsWhenTokenHasFiveFailedAttempts() {
        User user = new User(EMAIL);
        LoginToken loginToken = new LoginToken(user, "token-hash", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)));

        for (int i = 0; i < 5; i++) {
            loginToken.registerFailedAttempt();
        }

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        assertThrows(InvalidLoginException.class, () -> magicLinkService.verifyCode(EMAIL, CODE));
    }

    @Test
    void verifyCode_registersAttemptWhenCodeIsWrong() {
        User user = new User(EMAIL);
        LoginToken loginToken = new LoginToken(user, "token-hash", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)));
        int expectedAttempts = 1;

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        assertThrows(InvalidLoginException.class, () -> magicLinkService.verifyCode(EMAIL, "123123"));
        assertEquals(expectedAttempts, loginToken.getAttempts());
    }

    @Test
    void verifyCode_returnsUserWhenCodeIsRight() {
        User user = new User(EMAIL);
        LoginToken loginToken = new LoginToken(user, "token-hash", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        User result = magicLinkService.verifyCode(EMAIL, CODE);

        assertEquals(user, result);
        assertNotNull(loginToken.getUsedAt());
    }

    @Test
    void verifyCode_throwsWhenTokenIsExpired() {
        User user = new User(EMAIL);
        LoginToken loginToken = new LoginToken(user, "token-hash", CODE_HASH, Instant.now().minus(Duration.ofMinutes(15)));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        assertThrows(InvalidLoginException.class, () -> magicLinkService.verifyCode(EMAIL, CODE));
        assertNull(loginToken.getUsedAt());
    }
}