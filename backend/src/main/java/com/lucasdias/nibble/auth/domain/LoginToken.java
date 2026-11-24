package com.lucasdias.nibble.auth.domain;

import com.lucasdias.nibble.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "login_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column()
    private Instant usedAt;

    @Column(nullable = false)
    private int attempts;

    public LoginToken(User user, String tokenHash, String codeHash, Instant expiresAt) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
    }

    public void markUsed() {
        this.usedAt = Instant.now();
    }

    public void registerFailedAttempt() {
        this.attempts++;
    }

}