package com.cms.controller.tenant;

import com.cms.config.AppProperties;
import com.cms.dto.tenant.TenantRegisterRequest;
import com.cms.dto.tenant.TenantRegisterResponse;
import com.cms.model.central.tenant.Tenant;
import com.cms.service.central.tenant.TenantRegistrationService;
import com.cms.service.tenant.TenantAuthService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantRegistrationController {

  private final TenantRegistrationService registrationService;
  private final TenantAuthService tenantAuthService;
  private final AppProperties appProperties;

  @PostMapping("/register")
  public ResponseEntity<TenantRegisterResponse> register(
      @Valid @RequestBody TenantRegisterRequest tenantRegisterRequest) {
    Long tenantId = registrationService.validate(tenantRegisterRequest.getToken());
    Tenant tenant = tenantAuthService.register(tenantId, tenantRegisterRequest);
    registrationService.consume(tenantRegisterRequest.getToken());

    TenantRegisterResponse response =
        TenantRegisterResponse.builder()
            .tenantDomain(tenant.getDomain())
            .tenantName(tenant.getName())
            .loginUrl(buildTenantLoginUrl(tenant.getDomain()))
            .build();

    return ResponseEntity.ok(response);
  }

  private String buildTenantLoginUrl(String domain) {
    if (!StringUtils.hasText(domain)) {
      return null;
    }

    String sanitized = domain.trim().replaceAll("/+$", "");
    if (sanitized.startsWith("http://") || sanitized.startsWith("https://")) {
      return appendLoginPath(sanitized);
    }

    String scheme = "https";
    Integer port = null;
    String baseUrl = appProperties.getUrl();
    if (StringUtils.hasText(baseUrl)) {
      try {
        URI uri = URI.create(baseUrl.trim());
        if (uri.getScheme() != null) {
          scheme = uri.getScheme();
        }
        int uriPort = uri.getPort();
        if (uriPort > 0 && uriPort != 80 && uriPort != 443) {
          port = uriPort;
        }
      } catch (IllegalArgumentException ignored) {
        // fallback to defaults when app.url cannot be parsed
      }
    }

    StringBuilder builder = new StringBuilder();
    builder.append(scheme).append("://").append(sanitized);
    if (port != null && !sanitized.contains(":")) {
      builder.append(":").append(port);
    }
    builder.append("/login");
    return builder.toString();
  }

  private String appendLoginPath(String baseUrl) {
    String sanitized = baseUrl.replaceAll("/+$", "");
    return sanitized + "/login";
  }
}
