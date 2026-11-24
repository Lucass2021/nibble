package com.lucasdias.nibble.auth.domain;

import com.lucasdias.nibble.user.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoginToken> findByTokenHash(String tokenHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoginToken> findByUserAndCodeHash(User user, String codeHash);
}
