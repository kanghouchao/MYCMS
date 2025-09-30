package com.cms.controller.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cms.config.AppProperties;
import com.cms.dto.tenant.TenantRegisterRequest;
import com.cms.dto.tenant.TenantRegisterResponse;
import com.cms.model.central.tenant.Tenant;
import com.cms.service.central.tenant.TenantRegistrationService;
import com.cms.service.tenant.TenantAuthService;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TenantRegistrationControllerTest {

  @Mock private TenantRegistrationService registrationService;
  @Mock private TenantAuthService tenantAuthService;
  @Mock private AppProperties appProperties;

  @InjectMocks private TenantRegistrationController controller;

  private TenantRegisterRequest makeRequest() {
    TenantRegisterRequest req = new TenantRegisterRequest();
    req.setToken("token-123");
    req.setEmail("user@example.com");
    req.setPassword("password");
    return req;
  }

  private Tenant makeTenant(String domain, String name) {
    Tenant tenant = new Tenant();
    tenant.setId(42L);
    tenant.setDomain(domain);
    tenant.setName(name);
    return tenant;
  }

  @BeforeEach
  void setup() {
    when(registrationService.validate(any())).thenReturn(42L);
  }

  @Test
  void registerRespondsWithTenantLoginUrlUsingAppUrlSchemeAndPort() {
    TenantRegisterRequest request = makeRequest();
    Tenant tenant = makeTenant("tenant.example.com", "Tenant Example");

    when(tenantAuthService.register(42L, request)).thenReturn(tenant);
    when(appProperties.getUrl()).thenReturn("http://central.local:8080");

    ResponseEntity<TenantRegisterResponse> response = controller.register(request);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    assertThat(response.getBody()).isNotNull();
    TenantRegisterResponse body = Objects.requireNonNull(response.getBody());
    assertThat(body.getLoginUrl()).isEqualTo("http://tenant.example.com:8080/login");
    assertThat(body.getTenantDomain()).isEqualTo("tenant.example.com");
    assertThat(body.getTenantName()).isEqualTo("Tenant Example");

    verify(registrationService).consume("token-123");
  }

  @Test
  @SuppressWarnings("DataFlowIssue")
  void registerKeepsExistingSchemeWhenDomainHasProtocol() {
    TenantRegisterRequest request = makeRequest();
    Tenant tenant = makeTenant("https://tenant.partner.io/site", "Partner");

    when(tenantAuthService.register(42L, request)).thenReturn(tenant);

    ResponseEntity<TenantRegisterResponse> response = controller.register(request);

    assertThat(response.getBody()).isNotNull();
    TenantRegisterResponse body = Objects.requireNonNull(response.getBody());
    assertThat(body.getLoginUrl()).isEqualTo("https://tenant.partner.io/site/login");
  }
}
