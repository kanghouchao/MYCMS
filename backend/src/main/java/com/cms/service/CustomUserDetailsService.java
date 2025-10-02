package com.cms.service;

import com.cms.config.TenantContext;
import com.cms.repository.central.CentralUserRepository;
import com.cms.repository.tenant.TenantUserRepository;
import io.jsonwebtoken.lang.Collections;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final CentralUserRepository centralUserRepository;
  private final TenantUserRepository tenantUserRepository;
  private final TenantContext tenantContext;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (tenantContext.isTenant()) {
      String tenantId = tenantContext.getTenantId();
      if (!org.springframework.util.StringUtils.hasText(tenantId)) {
        throw new UsernameNotFoundException("Missing tenant context");
      }
      Long tenantIdLong;
      try {
        tenantIdLong = Long.valueOf(tenantId);
      } catch (NumberFormatException ex) {
        throw new UsernameNotFoundException("Invalid tenant ID format: " + tenantId);
      }
      return tenantUserRepository
          .findByTenant_IdAndEmail(tenantIdLong, username)
          .map(
              u -> {
                List<SimpleGrantedAuthority> authorities =
                    buildAuthorities(
                        u.getRoles(), r -> r.getName(), r -> r.getPermissions(), p -> p.getName());
                return buildUser(u.getEmail(), u.getPassword(), u.getEnabled(), authorities);
              })
          .orElseThrow(
              () ->
                  new UsernameNotFoundException(
                      "User not found (tenant): " + username + " @ tenant=" + tenantId));
    } else {
      return centralUserRepository
          .findByUsername(username)
          .map(
              u -> {
                List<SimpleGrantedAuthority> authorities =
                    buildAuthorities(
                        u.getRoles(), r -> r.getName(), r -> r.getPermissions(), p -> p.getName());
                return buildUser(u.getUsername(), u.getPassword(), u.getEnabled(), authorities);
              })
          .orElseThrow(
              () -> new UsernameNotFoundException("User not found (central): " + username));
    }
  }

  private <R, P> List<SimpleGrantedAuthority> buildAuthorities(
      Collection<R> roles,
      Function<R, String> roleNameExtractor,
      Function<R, Collection<P>> permExtractor,
      Function<P, String> permNameExtractor) {

    if (Collections.isEmpty(roles)) {
      return List.of();
    }

    return roles.stream()
        .flatMap(
            r -> {
              String roleName = roleNameExtractor.apply(r);
              Stream<String> roleAuth =
                  Stream.of("ROLE_" + (roleName == null ? "" : roleName.toUpperCase()));

              Collection<P> perms = permExtractor.apply(r);
              Stream<String> permOriginal =
                  perms == null ? Stream.empty() : perms.stream().map(permNameExtractor);
              Stream<String> permWithPrefix =
                  perms == null
                      ? Stream.empty()
                      : perms.stream()
                          .map(
                              p ->
                                  "PERM_"
                                      + (permNameExtractor.apply(p) == null
                                          ? ""
                                          : permNameExtractor.apply(p).toUpperCase()));

              return Stream.concat(roleAuth, Stream.concat(permOriginal, permWithPrefix));
            })
        .distinct()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  private UserDetails buildUser(
      String username, String password, boolean enabled, List<SimpleGrantedAuthority> authorities) {
    return User.withUsername(username)
        .password(password)
        .disabled(!enabled)
        .authorities(authorities)
        .build();
  }
}
