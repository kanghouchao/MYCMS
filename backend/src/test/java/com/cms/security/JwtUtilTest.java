package com.cms.security;

import com.cms.config.AppProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JwtUtil}.
 *
 * These tests do not start a Spring context. AppProperties is mocked so tests
 * are
 * fast and deterministic.
 */
class JwtUtilTest {

    private static final String SECRET = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";

    private AppProperties props;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        props = mock(AppProperties.class);
        // use a reasonably long secret required by JJWT for HMAC
        when(props.getJwtSecret()).thenReturn(SECRET);
        // default to 1 hour for happy-path tests
        when(props.getJwtExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(props);
    }

    @Test
    void generateToken_and_validateToken_success() {
        String username = "alice";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token, "generated token should not be null");

        boolean valid = jwtUtil.validateToken(token, username);
        assertTrue(valid, "token should validate for correct username");
    }

    @Test
    void validateToken_returnsFalse_forIncorrectUsername() {
        String token = jwtUtil.generateToken("alice");
        // same token but different username should not validate
        assertFalse(jwtUtil.validateToken(token, "bob"));
    }

    @Test
    void validateToken_throws_forExpiredToken() {
        // make the token already expired by returning a negative expiration
        when(props.getJwtExpiration()).thenReturn(-10L);
        JwtUtil utilWithShortExpiry = new JwtUtil(props);

        String token = utilWithShortExpiry.generateToken("alice");

        // parsing an expired token will throw ExpiredJwtException from the parser
        assertThrows(ExpiredJwtException.class, () -> utilWithShortExpiry.validateToken(token, "alice"));
    }

    @Test
    void getClaims_or_validateToken_throws_forInvalidSignature() {
        String token = jwtUtil.generateToken("alice");

        // create a JwtUtil with a different secret -> signature verification should
        // fail
        AppProperties otherProps = mock(AppProperties.class);
        when(otherProps.getJwtSecret()).thenReturn("DIFFERENT_SECRET_which_is_also_long_enough_012345");
        when(otherProps.getJwtExpiration()).thenReturn(3600L);
        JwtUtil otherUtil = new JwtUtil(otherProps);

        // parsing / validation should throw a JwtException (signature / format error)
        assertThrows(JwtException.class, () -> otherUtil.getClaims(token));
        assertThrows(JwtException.class, () -> otherUtil.validateToken(token, "alice"));
    }

}
