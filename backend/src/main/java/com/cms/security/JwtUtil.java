package com.cms.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.java.Log;

import java.security.Key;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Log
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;


    public String generateToken(String username) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + expiration * 1000);
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
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
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, String username) {
        Claims claims = getClaims(token);
        return claims.getSubject().equals(username) && !claims.getExpiration().before(new Date());
    }
}
