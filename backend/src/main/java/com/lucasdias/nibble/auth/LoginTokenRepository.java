package com.lucasdias.nibble.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {

    Optional<LoginToken> findByTokenHash(String tokenHash);
}
