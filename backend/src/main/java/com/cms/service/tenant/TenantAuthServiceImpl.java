package com.cms.service.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.cms.dto.auth.Token;
import com.cms.utils.JwtUtil;

@Service
@RequiredArgsConstructor
public class TenantAuthServiceImpl implements TenantAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public Token login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        return jwtUtil.generateToken(auth.getName());
    }
}
