package com.cms.utils;

import com.cms.config.AppProperties;
import com.cms.dto.auth.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  private final AppProperties appProperties;

  public Token generateToken(
      @NonNull String subject, @Nullable String issuer, @NonNull Map<String, Object> claims) {
    long nowMillis = System.currentTimeMillis();
    Date now = new Date(nowMillis);
    Date exp = new Date(nowMillis + appProperties.getJwtExpiration() * 1000);
    Key key = Keys.hmacShaKeyFor(appProperties.getJwtSecret().getBytes());
    String token =
        Jwts.builder()
            .claims()
            .issuer(issuer)
            .subject(subject)
            .issuedAt(now)
            .expiration(exp)
            .add(claims)
            .and()
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
}
