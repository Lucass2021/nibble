package com.lucasdias.nibble.auth.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.lucasdias.nibble.auth.domain.*;
import com.lucasdias.nibble.user.domain.User;
import com.lucasdias.nibble.user.domain.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private EmailSender emailSender;

    @Mock
    private MagicLinkProperties properties;

    @InjectMocks
    private MagicLinkService magicLinkService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    }

    @Test
    void requestLink_throwsWhenThreeLinksAreActive() {
        when(loginTokenRepository.countByUserAndExpiresAtAfter(eq(user), any(Instant.class)))
                .thenReturn(3L);

        assertThrows(TooManyLoginRequestsException.class, () -> magicLinkService.requestLink(EMAIL));

        verify(emailSender, never()).sendLoginEmail(anyString(), anyString(), anyString());
    }

    @Test
    void verifyCode_throwsWhenTokenHasFiveFailedAttempts() {
        LoginToken loginToken =
                new LoginToken(user, "token-hash", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)));

        for (int i = 0; i < 5; i++) {
            loginToken.registerFailedAttempt();
        }

        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        assertThrows(InvalidLoginException.class, () -> magicLinkService.verifyCode(EMAIL, CODE));
    }

    @Test
    void verifyCode_registersAttemptWhenCodeIsWrong() {
        LoginToken loginToken =
                new LoginToken(user, "token-hash", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)));
        int expectedAttempts = 1;

        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        assertThrows(InvalidLoginException.class, () -> magicLinkService.verifyCode(EMAIL, "123123"));
        assertEquals(expectedAttempts, loginToken.getAttempts());
    }

    @Test
    void verifyCode_returnsUserWhenCodeIsRight() {
        LoginToken loginToken =
                new LoginToken(user, "token-hash", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)));

        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        User result = magicLinkService.verifyCode(EMAIL, CODE);

        assertEquals(user, result);
        assertNotNull(loginToken.getUsedAt());
    }

    @Test
    void verifyCode_throwsWhenTokenIsExpired() {
        LoginToken loginToken =
                new LoginToken(user, "token-hash", CODE_HASH, Instant.now().minus(Duration.ofMinutes(15)));

        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        assertThrows(InvalidLoginException.class, () -> magicLinkService.verifyCode(EMAIL, CODE));
        assertNull(loginToken.getUsedAt());
    }

    @Test
    void verifyCode_throwsWhenTokenWasAlreadyUsed() {
        LoginToken loginToken =
                new LoginToken(user, "token-hash", CODE_HASH, Instant.now().plus(Duration.ofMinutes(15)));
        loginToken.markUsed();

        when(loginTokenRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(loginToken));

        assertThrows(InvalidLoginException.class, () -> magicLinkService.verifyCode(EMAIL, CODE));
    }
}
