package com.example.todo.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import com.example.todo.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtService {

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                JwtConfig.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createToken(Long userId) {
        Date now = new Date();

        Date expiration = new Date(
                now.getTime()
                        + JwtConfig.getExpirationMilliseconds()
        );

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public boolean isValid(String token) {
        try {
            getUserId(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}