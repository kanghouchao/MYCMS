package com.cms.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cms.dto.auth.Token;

@SpringBootTest
@SuppressWarnings("deprecation")
class JwtUtilValidateTests {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void validateTokenReturnsTrueForMatchingSubject() {
        Token t = jwtUtil.generateToken("alice", "TestIssuer", Map.of("role", "USER"));
        assertThat(jwtUtil.validateToken(t.token(), "alice")).isTrue();
    }

    @Test
    void validateTokenReturnsFalseForMismatchedSubject() {
        Token t = jwtUtil.generateToken("bob", "TestIssuer", Map.of());
        assertThat(jwtUtil.validateToken(t.token(), "alice")).isFalse();
    }
}
