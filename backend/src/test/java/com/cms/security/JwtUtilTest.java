package com.cms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testGenerateAndValidateToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        assertNotNull(token);
        Claims claims = jwtUtil.getClaims(token);
        assertEquals(username, claims.getSubject());
        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void testValidateTokenWithWrongUsername() {
        String token = jwtUtil.generateToken("user1");
        assertFalse(jwtUtil.validateToken(token, "user2"));
    }

    @Test
    void testExpiredToken() throws Exception {
        String username = "expireduser";
        java.lang.reflect.Field field = JwtUtil.class.getDeclaredField("expiration");
        field.setAccessible(true);
        long original = field.getLong(jwtUtil);
        try {
            field.set(jwtUtil, 1L);
            String token = jwtUtil.generateToken(username);
            Thread.sleep(1500);
            assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateToken(token, username));
        } finally {
            field.set(jwtUtil, original);
        }
    }
}
