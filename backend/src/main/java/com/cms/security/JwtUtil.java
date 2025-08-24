package com.cms.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.security.Key;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import com.cms.config.AppProperties;

import java.util.Date;

@Component
@Log
@RequiredArgsConstructor
public class JwtUtil {

    private final AppProperties appProperties;

    public String generateToken(String username) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + appProperties.getJwtExpiration() * 1000);
        Key key = Keys.hmacShaKeyFor(appProperties.getJwtSecret().getBytes());
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        log.info("Getting claims from token");
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(appProperties.getJwtSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, String username) {
        Claims claims = getClaims(token);
        return claims.getSubject().equals(username) && !claims.getExpiration().before(new Date());
    }
}
