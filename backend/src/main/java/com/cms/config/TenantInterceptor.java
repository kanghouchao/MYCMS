package com.cms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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
    String role = request.getHeader("X-Role");
    String tenantId = request.getHeader("X-Tenant-ID");
    log.debug("Incoming request role: {}, tenant: {}", role, tenantId);
    String uri = request.getRequestURI();
    String roleLower = role == null ? null : role.toLowerCase();

    // Enforce role/path alignment
    if (uri != null && uri.startsWith("/tenant")) {
      if (!"tenant".equals(roleLower)) {
        response.sendError(
            HttpServletResponse.SC_BAD_REQUEST, "X-Role must be 'tenant' for /tenant endpoints");
        return false;
      }
      if (!StringUtils.hasText(tenantId)) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Tenant-ID header");
        return false;
      }
      tenantContext.setTenantId(tenantId);
      ThreadContext.put("tenantId", tenantId);
      return true;
    }

    if (uri != null && uri.startsWith("/central")) {
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
