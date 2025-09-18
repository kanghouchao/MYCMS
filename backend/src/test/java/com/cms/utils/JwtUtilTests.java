package com.cms.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cms.dto.auth.Token;

import io.jsonwebtoken.Claims;

@SpringBootTest
class JwtUtilTests {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generateAndParseToken() {
        Token token = jwtUtil.generateToken("user1", "CentralAuth", Map.of("role", "ADMIN"));
        Claims claims = jwtUtil.getClaims(token.token());
        assertThat(claims.getSubject()).isEqualTo("user1");
        assertThat(claims.getIssuer()).isEqualTo("CentralAuth");
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.getExpiration()).isNotNull();
    }
}
