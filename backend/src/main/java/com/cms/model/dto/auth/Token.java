package com.cms.model.dto.auth;

public record Token(String token, long expiresAt) {}
