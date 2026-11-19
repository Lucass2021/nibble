package com.lucasdias.nibble.auth;

import com.lucasdias.nibble.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-minutes}")
    private long expirationMinutes;

    public String generate(User user){
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    public Long parseUserId(String jwt) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
        return Long.valueOf(claims.getSubject());
    }
}
