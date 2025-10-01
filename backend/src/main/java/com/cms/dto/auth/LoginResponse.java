package com.cms.dto.auth;

public record LoginResponse(String token, long expiresAt, String role, String redirectPath) {

  public LoginResponse(Token token, String role, String redirectPath) {
    this(token.token(), token.expiresAt(), role, redirectPath);
  }
}
