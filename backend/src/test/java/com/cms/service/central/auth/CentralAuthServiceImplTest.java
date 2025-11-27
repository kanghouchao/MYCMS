package com.cms.service.central.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cms.model.dto.auth.Token;
import com.cms.utils.JwtUtil;
import java.util.List;
import java.util.Map;
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

@ExtendWith(MockitoExtension.class)
class CentralAuthServiceImplTest {

  @Mock AuthenticationManager authenticationManager;
  @Mock JwtUtil jwtUtil;
  @Mock Authentication authentication;

  @Captor ArgumentCaptor<String> subjectCaptor;
  @Captor ArgumentCaptor<String> issuerCaptor;
  @Captor ArgumentCaptor<Map<String, Object>> claimsCaptor;

  @InjectMocks CentralAuthServiceImpl service;

  @Test
  void loginGeneratesTokenWithAuthorities() {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(authentication.getName()).thenReturn("alice");
    when(authentication.getAuthorities())
        .thenAnswer(inv -> List.<GrantedAuthority>of(() -> "ROLE_ADMIN"));
    when(jwtUtil.generateToken(any(), any(), any()))
        .thenReturn(new Token("jwt", System.currentTimeMillis() + 1000));

    Token token = service.login("alice", "password");
    assertThat(token).isNotNull();
    assertThat(token.token()).isEqualTo("jwt");

    verify(jwtUtil)
        .generateToken(subjectCaptor.capture(), issuerCaptor.capture(), claimsCaptor.capture());
    assertThat(subjectCaptor.getValue()).isEqualTo("alice");
    assertThat(issuerCaptor.getValue()).isEqualTo("CentralAuth");
    Object auths = claimsCaptor.getValue().get("authorities");
    assertThat(auths).isInstanceOf(List.class);
    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) auths;
    assertThat(list).containsExactly("ROLE_ADMIN");
  }
}
