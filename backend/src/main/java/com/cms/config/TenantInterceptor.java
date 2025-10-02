package com.cms.config;

import com.cms.constants.RequestContextKeys;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
@Component
public class TenantInterceptor implements HandlerInterceptor {

  private final TenantContext tenantContext;

  public TenantInterceptor(TenantContext tenantContext) {
    this.tenantContext = tenantContext;
  }

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler)
      throws Exception {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Claims claims = null;
    if (authentication != null && authentication.getDetails() instanceof Claims c) {
      claims = c;
    }
    String claimedTenantId = claims != null ? claims.get("tenantId", String.class) : null;

    String role = request.getHeader(RequestContextKeys.HEADER_ROLE);
    String tenantId = request.getHeader(RequestContextKeys.HEADER_TENANT_ID);

    if (!StringUtils.hasText(role) || !StringUtils.hasText(tenantId)) {
      var cookies = request.getCookies();
      if (cookies != null) {
        for (var cookie : cookies) {
          if (!StringUtils.hasText(role)
              && RequestContextKeys.COOKIE_ROLE.equalsIgnoreCase(cookie.getName())
              && StringUtils.hasText(cookie.getValue())) {
            role = cookie.getValue();
          }
          if (!StringUtils.hasText(tenantId)
              && RequestContextKeys.COOKIE_TENANT_ID.equalsIgnoreCase(cookie.getName())
              && StringUtils.hasText(cookie.getValue())) {
            tenantId = cookie.getValue();
          }
        }
      }
    }

    String uri = request.getRequestURI();
    boolean isTenantLogin = "/tenant/login".equals(uri);

    if (!StringUtils.hasText(tenantId) && StringUtils.hasText(claimedTenantId)) {
      tenantId = claimedTenantId;
    }

    log.debug(
        "Incoming request role: {}, tenant: {}, claimed-tenant: {}",
        role,
        tenantId,
        claimedTenantId);
    String roleLower = role == null ? null : role.toLowerCase();

    // Enforce role/path alignment
    if (uri != null && uri.startsWith("/tenant")) {
      if (StringUtils.hasText(roleLower) && !"tenant".equals(roleLower)) {
        response.sendError(
            HttpServletResponse.SC_BAD_REQUEST, "X-Role must be 'tenant' for /tenant endpoints");
        return false;
      }
      if (!StringUtils.hasText(tenantId)) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Tenant-ID header");
        return false;
      }
      boolean tenantMismatch =
          StringUtils.hasText(claimedTenantId) && !Objects.equals(claimedTenantId, tenantId);
      if (tenantMismatch && !isTenantLogin) {
        response.sendError(
            HttpServletResponse.SC_FORBIDDEN, "Tenant scope mismatch between token and request");
        return false;
      }
      tenantContext.setTenantId(tenantId);
      ThreadContext.put("tenantId", tenantId);
      return true;
    }

    if (uri != null && uri.startsWith("/central")) {
      if (!StringUtils.hasText(roleLower)) {
        roleLower = "central";
      }
      if (!"central".equals(roleLower)) {
        response.sendError(
            HttpServletResponse.SC_BAD_REQUEST, "X-Role must be 'central' for /central endpoints");
        return false;
      }
      tenantContext.clear();
      ThreadContext.put("tenantId", "central");
      return true;
    }

    // Other paths: default to central context (no tenant)
    tenantContext.clear();
    ThreadContext.put("tenantId", "public");
    return true;
  }

  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      @Nullable Exception ex) {
    tenantContext.clear();
    ThreadContext.remove("tenantId");
  }
}
