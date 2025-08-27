package com.cms.service.central;

import com.cms.config.AppProperties;
import com.cms.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.cms.dto.auth.LoginResponse;

@Service
@RequiredArgsConstructor
public class CentralAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AppProperties appProperties;

    public LoginResponse login(String username, String password) throws AuthenticationException {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        String token = jwtUtil.generateToken(auth.getName());
        long expiresAt = System.currentTimeMillis() / 1000 + appProperties.getJwtExpiration();
        return new LoginResponse(token, expiresAt);
    }
}
