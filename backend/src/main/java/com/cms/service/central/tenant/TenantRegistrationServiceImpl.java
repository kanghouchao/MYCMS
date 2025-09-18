package com.cms.service.central.tenant;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantRegistrationServiceImpl implements TenantRegistrationService {

  private final StringRedisTemplate redisTemplate;
  private static final SecureRandom random = new SecureRandom();
  private static final Duration TTL = Duration.ofDays(7);

  private String keyFor(String token) {
    return "tenant:registration:" + token;
  }

  @Override
  public String createToken(Long tenantId) {
    byte[] b = new byte[32];
    random.nextBytes(b);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    String key = keyFor(token);
    redisTemplate.opsForValue().set(key, String.valueOf(tenantId), TTL);
    return token;
  }

  @Override
  public Long validate(String token) {
    String key = keyFor(token);
    String val = redisTemplate.opsForValue().get(key);
    if (val == null) return null;
    try {
      return Long.valueOf(val);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public void consume(String token) {
    redisTemplate.delete(keyFor(token));
  }
}
