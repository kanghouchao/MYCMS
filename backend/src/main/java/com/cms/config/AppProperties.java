package com.cms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private Scheme scheme = Scheme.HTTP;

  private String domain;

  /** app.jwt.* */
  private Jwt jwt = new Jwt();

  @Getter
  @Setter
  public static class Jwt {
    private String secret;
    private long expiration;
  }

  public enum Scheme {
    HTTP("http"),
    HTTPS("https");

    private final String name;

    Scheme(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public String getScheme() {
    return scheme != null ? scheme.getName() : null;
  }

  public String getJwtSecret() {
    return jwt != null ? jwt.getSecret() : null;
  }

  public long getJwtExpiration() {
    return jwt != null ? jwt.getExpiration() : 0L;
  }
}
