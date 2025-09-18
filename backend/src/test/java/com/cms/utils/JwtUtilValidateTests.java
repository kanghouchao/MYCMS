package com.cms.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.cms.dto.auth.Token;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtUtilValidateTests {

  @Autowired private JwtUtil jwtUtil;

  @Test
  void validateTokenReturnsTrueForMatchingSubject() {
    Token t = jwtUtil.generateToken("alice", "TestIssuer", Map.of("role", "USER"));
    var claims = jwtUtil.getClaims(t.token());
    assertThat(claims.getSubject()).isEqualTo("alice");
    assertThat(claims.getExpiration()).isNotNull();
  }

  @Test
  void validateTokenReturnsFalseForMismatchedSubject() {
    Token t = jwtUtil.generateToken("bob", "TestIssuer", Map.of());
    var claims = jwtUtil.getClaims(t.token());
    assertThat(claims.getSubject()).isNotEqualTo("alice");
  }
}
