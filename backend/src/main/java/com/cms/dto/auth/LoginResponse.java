package com.cms.dto.auth;

public record LoginResponse(String token, long expiresAt) {

  public LoginResponse(Token token) {
    this(token.token(), token.expiresAt());
  }
}
