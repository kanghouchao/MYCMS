package com.cms.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JwtAuthenticationFilterTest {

    @Autowired
    private JwtAuthenticationFilter filter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("alice");
        when(jwtUtil.getClaims(token)).thenReturn(claims);
        when(jwtUtil.validateToken(token, "alice")).thenReturn(true);

        UserDetails user = User.withUsername("alice").password("pass").roles("USER").build();
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(user);

        FilterChain chain = (req, res) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth, "Authentication should be set before reaching the filter chain");
            assertEquals(user.getUsername(), auth.getName());
        };

        filter.doFilterInternal(request, response, chain);

        Authentication after = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(after);
        assertEquals("alice", after.getName());
    }

    @Test
    void doFilterInternal_invalidToken_doesNotSetAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "bad-token";
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("bob");
        when(jwtUtil.getClaims(token)).thenReturn(claims);
        when(jwtUtil.validateToken(token, "bob")).thenReturn(false);

        when(userDetailsService.loadUserByUsername("bob")).thenReturn(User.withUsername("bob").password("p").roles("USER").build());

        FilterChain chain = (req, res) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNull(auth, "Authentication should NOT be set for invalid token");
        };

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_noHeader_doesNotSetAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNull(auth, "Authentication should be null when no Authorization header");
        };

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
