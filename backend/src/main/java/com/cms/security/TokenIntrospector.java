package com.cms.security;

import com.cms.dto.auth.Token;
import com.cms.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Utility component to validate and introspect JWT tokens from incoming requests. */
@Log4j2
@Component
@RequiredArgsConstructor
public class TokenIntrospector {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String TOKEN_COOKIE = "token";
  private static final String BLACKLIST_PREFIX = "blacklist:tokens:";

  private final JwtUtil jwtUtil;
  private final RedisTemplate<String, Object> redisTemplate;

  public Optional<TokenDetails> introspect(HttpServletRequest request) {
    return introspect(extractToken(request));
  }

  public Optional<TokenDetails> introspect(String tokenValue) {
    if (!StringUtils.hasText(tokenValue)) {
      return Optional.empty();
    }
    try {
      Claims claims = jwtUtil.getClaims(tokenValue);
      Date expiration = claims.getExpiration();
      if (expiration == null || expiration.before(new Date())) {
        log.debug("Token is expired or has no expiration");
        return Optional.empty();
      }
      if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + tokenValue))) {
        log.debug("Token {} is blacklisted", tokenValue);
        return Optional.empty();
      }
      String issuer = claims.getIssuer();
      return Optional.of(new TokenDetails(tokenValue, expiration.getTime(), issuer));
    } catch (JwtException ex) {
      log.debug("Failed to parse token: {}", ex.getMessage());
      return Optional.empty();
    }
  }

  private String extractToken(HttpServletRequest request) {
    if (request == null) {
      return null;
    }
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
      return authHeader.substring(BEARER_PREFIX.length());
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (TOKEN_COOKIE.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  public record TokenDetails(String token, long expiresAt, String issuer) {
    public Token asToken() {
      return new Token(token, expiresAt);
    }
  }
}
