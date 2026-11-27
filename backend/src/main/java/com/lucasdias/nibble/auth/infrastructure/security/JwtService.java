package com.lucasdias.nibble.auth.infrastructure.security;

import com.lucasdias.nibble.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
    }

    public String generate(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.expiration());

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    public Long parseUserId(String jwt) {
        Claims claims =
                Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
        return Long.valueOf(claims.getSubject());
    }
}
