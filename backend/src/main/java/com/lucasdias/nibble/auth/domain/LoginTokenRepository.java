package com.lucasdias.nibble.auth.domain;

import com.lucasdias.nibble.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {

    Optional<LoginToken> findByTokenHash(String tokenHash);
    Optional<LoginToken> findByUserAndCodeHash(User user, String codeHash);
}
