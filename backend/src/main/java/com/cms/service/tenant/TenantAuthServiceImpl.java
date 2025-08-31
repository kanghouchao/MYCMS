package com.cms.service.tenant;

import lombok.RequiredArgsConstructor;

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
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        return jwtUtil.generateToken(auth.getName());
    }

    @Override
    @Transactional
    public void register(Long tenantId, TenantRegisterRequest tenantRegisterRequest) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        TenantUser entity = new TenantUser();
        entity.setNickname(tenantRegisterRequest.getToken());
        entity.setTenant(tenant);
        entity.setEmail(tenantRegisterRequest.getEmail());
        entity.setPassword(passwordEncoder.encode(tenantRegisterRequest.getPassword()));
        userRepository.save(entity);
    }
}
