package com.cms.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import com.cms.config.AppProperties;
import com.cms.dto.auth.Token;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final AppProperties appProperties;

    public Token generateToken(String username) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + appProperties.getJwtExpiration() * 1000);
        Key key = Keys.hmacShaKeyFor(appProperties.getJwtSecret().getBytes());
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
        return new Token(token, exp.getTime());
    }

    public Claims getClaims(String token) {
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
