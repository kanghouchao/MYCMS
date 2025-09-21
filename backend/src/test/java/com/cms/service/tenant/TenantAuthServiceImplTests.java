package com.cms.service.tenant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cms.dto.tenant.TenantRegisterRequest;
import com.cms.model.central.tenant.Tenant;
import com.cms.model.tenant.security.TenantUser;
import com.cms.repository.central.TenantRepository;
import com.cms.repository.tenant.TenantUserRepository;
import com.cms.utils.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

class TenantAuthServiceImplTests {

  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtUtil jwtUtil;
  @Mock private TenantUserRepository userRepository;
  @Mock private TenantRepository tenantRepository;

  @InjectMocks private TenantAuthServiceImpl service;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void registerSavesUserWithEncodedPasswordAndNickname() {
    Tenant t = new Tenant();
    when(tenantRepository.findById(1L)).thenReturn(Optional.of(t));

    TenantRegisterRequest req = new TenantRegisterRequest();
    req.setEmail("bob@example.com");
    req.setPassword("rawpw");

    when(passwordEncoder.encode("rawpw")).thenReturn("encoded");

    service.register(1L, req);

    verify(userRepository).save(any(TenantUser.class));
  }
}
