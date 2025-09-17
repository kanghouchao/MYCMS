package com.cms.service.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cms.dto.auth.Token;
import com.cms.dto.tenant.TenantRegisterRequest;
import com.cms.model.central.tenant.Tenant;
import com.cms.model.tenant.security.TenantUser;
import com.cms.repository.central.TenantRepository;
import com.cms.repository.tenant.TenantUserRepository;
import com.cms.utils.JwtUtil;

@Service
@Log4j2
@RequiredArgsConstructor
public class TenantAuthServiceImpl implements TenantAuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TenantUserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public Token login(String username, String password) {
        log.debug("Attempting login for user: {}", username);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        return jwtUtil.generateToken(auth.getName(),
                "TenantAuth",
                Map.of("authorities",
                        auth.getAuthorities().stream().map(a -> a.getAuthority()).toList()));
    }

    @Override
    @Transactional
    public void register(Long tenantId, TenantRegisterRequest tenantRegisterRequest) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        TenantUser entity = new TenantUser();
        // Derive a default nickname from email local-part instead of using the
        // registration token
        String email = tenantRegisterRequest.getEmail();
        String nickname = (email != null && email.contains("@")) ? email.substring(0, email.indexOf('@')) : "admin";
        entity.setNickname(nickname);
        entity.setTenant(tenant);
        entity.setEmail(email);
        entity.setPassword(passwordEncoder.encode(tenantRegisterRequest.getPassword()));
        userRepository.save(entity);
    }
}
