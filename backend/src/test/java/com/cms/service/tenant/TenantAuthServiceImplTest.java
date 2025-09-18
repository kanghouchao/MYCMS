package com.cms.service.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cms.dto.auth.Token;
import com.cms.dto.tenant.TenantRegisterRequest;
import com.cms.model.central.tenant.Tenant;
import com.cms.model.tenant.security.TenantUser;
import com.cms.repository.central.TenantRepository;
import com.cms.repository.tenant.TenantUserRepository;
import com.cms.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class TenantAuthServiceImplTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    TenantUserRepository userRepository;
    @Mock
    TenantRepository tenantRepository;
    @Mock
    Authentication authentication;

    @Captor
    ArgumentCaptor<TenantUser> userCaptor;

    @InjectMocks
    TenantAuthServiceImpl service;

    @Test
    void loginReturnsTokenWithAuthorities() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("bob");
        when(authentication.getAuthorities()).thenAnswer(inv -> List.<GrantedAuthority>of(() -> "ROLE_USER"));
        when(jwtUtil.generateToken(any(), any(), any()))
                .thenReturn(new Token("jwt-t", System.currentTimeMillis() + 1000));

        Token t = service.login("bob", "pwd");
        assertThat(t.token()).isEqualTo("jwt-t");
    }

    @Test
    void registerCreatesTenantUserWithEncodedPasswordAndNickname() {
        Tenant tenant = new Tenant();
        tenant.setId(123L);
        when(tenantRepository.findById(123L)).thenReturn(Optional.of(tenant));
        when(passwordEncoder.encode("secret")).thenReturn("enc-secret");

        TenantRegisterRequest req = new TenantRegisterRequest();
        req.setEmail("admin@example.com");
        req.setPassword("secret");

        service.register(123L, req);

        verify(userRepository).save(userCaptor.capture());
        TenantUser saved = userCaptor.getValue();
        assertThat(saved.getTenant()).isEqualTo(tenant);
        assertThat(saved.getEmail()).isEqualTo("admin@example.com");
        assertThat(saved.getPassword()).isEqualTo("enc-secret");
        assertThat(saved.getNickname()).isEqualTo("admin");
    }
}
