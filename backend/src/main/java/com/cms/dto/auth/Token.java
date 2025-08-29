package com.cms.dto.auth;

public record Token(
        String token,
        long expiresAt) {
}
