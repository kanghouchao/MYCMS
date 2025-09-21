package com.cms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.cms.config.TenantContext;
import com.cms.model.central.security.CentralPermission;
import com.cms.model.central.security.CentralRole;
import com.cms.model.central.security.CentralUser;
import com.cms.model.tenant.security.TenantUser;
import com.cms.repository.central.CentralUserRepository;
import com.cms.repository.tenant.TenantUserRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTests {

  @Mock private CentralUserRepository centralUserRepository;
  @Mock private TenantUserRepository tenantUserRepository;
  @Mock private TenantContext tenantContext;

  @InjectMocks private CustomUserDetailsService service;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void loadUserByUsername_centralFound_buildsUserAndAuthorities() {
    CentralUser u = new CentralUser();
    u.setUsername("centralUser");
    u.setPassword("pw");
    u.setEnabled(true);

    CentralRole r = new CentralRole();
    r.setName("admin");
    CentralPermission p = new CentralPermission();
    p.setName("read");
    r.setPermissions(Set.of(p));
    u.setRoles(Set.of(r));

    when(tenantContext.isTenant()).thenReturn(false);
    when(centralUserRepository.findByUsername("centralUser")).thenReturn(Optional.of(u));

    UserDetails ud = service.loadUserByUsername("centralUser");
    assertThat(ud.getUsername()).isEqualTo("centralUser");
    assertThat(ud.getPassword()).isEqualTo("pw");
    assertThat(ud.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    assertThat(ud.getAuthorities()).anyMatch(a -> a.getAuthority().equals("read"));
    assertThat(ud.getAuthorities()).anyMatch(a -> a.getAuthority().equals("PERM_READ"));
  }

  @Test
  void loadUserByUsername_centralNotFound_throws() {
    when(tenantContext.isTenant()).thenReturn(false);
    when(centralUserRepository.findByUsername("nope")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nope"));
  }

  @Test
  void loadUserByUsername_tenantFound_buildsUser() {
    TenantUser tu = new TenantUser();
    tu.setEmail("tuser@example.com");
    tu.setPassword("tpw");
    tu.setEnabled(true);

    when(tenantContext.isTenant()).thenReturn(true);
    when(tenantContext.getTenantId()).thenReturn("42");
    when(tenantUserRepository.findByTenant_IdAndEmail(eq(42L), eq("tuser@example.com")))
        .thenReturn(Optional.of(tu));

    UserDetails ud = service.loadUserByUsername("tuser@example.com");
    assertThat(ud.getUsername()).isEqualTo("tuser@example.com");
    assertThat(ud.getPassword()).isEqualTo("tpw");
  }

  @Test
  void loadUserByUsername_tenantMissingTenantId_throws() {
    when(tenantContext.isTenant()).thenReturn(true);
    when(tenantContext.getTenantId()).thenReturn("");

    assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("a"));
  }

  @Test
  void loadUserByUsername_tenantInvalidTenantId_throws() {
    when(tenantContext.isTenant()).thenReturn(true);
    when(tenantContext.getTenantId()).thenReturn("notANumber");

    assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("a"));
  }

  @Test
  void buildAuthorities_handlesEmptyAndNulls() {
    CentralUser u = new CentralUser();
    u.setUsername("x");
    u.setPassword("p");
    u.setEnabled(true);
    // role with null name and null permissions
    CentralRole r = new CentralRole();
    r.setName(null);
    r.setPermissions(null);
    u.setRoles(Set.of(r));

    when(tenantContext.isTenant()).thenReturn(false);
    when(centralUserRepository.findByUsername("x")).thenReturn(Optional.of(u));

    UserDetails ud = service.loadUserByUsername("x");
    // should still create a ROLE_ authority (with empty suffix) and no NPE
    assertThat(ud.getAuthorities()).anyMatch(a -> a.getAuthority().startsWith("ROLE_"));
  }
}
