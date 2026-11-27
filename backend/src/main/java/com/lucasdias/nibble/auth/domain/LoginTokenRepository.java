package com.lucasdias.nibble.auth.domain;

import com.lucasdias.nibble.user.domain.User;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoginToken> findByTokenHash(String tokenHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoginToken> findFirstByUserOrderByIdDesc(User user);

    long countByUserAndExpiresAtAfter(User user, Instant now);
}
