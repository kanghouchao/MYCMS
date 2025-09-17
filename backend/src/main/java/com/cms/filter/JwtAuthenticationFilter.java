package com.cms.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cms.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Collections;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.getClaims(token);
            if (new Date().before(claims.getExpiration())) {
                String username = claims.getSubject();
                List<?> authorities = claims.get("authorities", List.class);
                if (!Collections.isEmpty(authorities)) {
                    PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                            username, token, authorities.stream()
                                    .map(a -> new SimpleGrantedAuthority(String.valueOf(a)))
                                    .toList());
                    authentication.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
