package com.gucardev.springreactboilerplate.infra.config.security.jwt;

import com.gucardev.springreactboilerplate.domain.user.entity.Role;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mints and validates HS256 access tokens. The token subject is the user's email; the user id and
 * granted authorities ride along as claims for client convenience. Authentication itself always
 * reloads the user from the database (see {@code JwtAuthenticationFilter}), so the claims are
 * informational, never the source of truth for authorization.
 */
@Slf4j
@Service
public class JwtService {

    private final SecretKey key;
    private final long tokenValidityMillis;

    public JwtService(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.tokenValidityMillis = properties.getTokenValidityInMinutes() * 60_000L;
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenValidityMillis);
        List<String> authorities = user.getRoles().stream()
                .map(Role::getName)
                .map(name -> "ROLE_" + name)
                .toList();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("roles", authorities)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Rejected JWT: {}", ex.getMessage());
            return false;
        }
    }

    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    public long getAccessTokenValiditySeconds() {
        return tokenValidityMillis / 1000L;
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
