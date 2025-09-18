package com.cms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  /** Application base URL (for links in notifications, etc.) */
  private String url;

  /** Nested JWT configuration bound from app.jwt.* */
  @NestedConfigurationProperty private Jwt jwt = new Jwt();

  @Getter
  @Setter
  public static class Jwt {
    private String secret;
    private long expiration;
  }

  // Backward-compatible accessors used by existing components (e.g., JwtUtil)
  public String getJwtSecret() {
    return jwt != null ? jwt.getSecret() : null;
  }

  public long getJwtExpiration() {
    return jwt != null ? jwt.getExpiration() : 0L;
  }
}
