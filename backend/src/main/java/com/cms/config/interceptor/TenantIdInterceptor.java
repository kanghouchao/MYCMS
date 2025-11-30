package com.cms.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
@Component
@AllArgsConstructor
public class TenantIdInterceptor implements HandlerInterceptor {

  private final TenantContext tenantContext;

  private static final String HEADER_ROLE = "X-Role";
  private static final String HEADER_TENANT_ID = "X-Tenant-ID";
  private static final String HEADER_ROLE_TENANT = "tenant";

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    if (HEADER_ROLE_TENANT.equals(request.getHeader(HEADER_ROLE))
        && StringUtils.isNoneBlank(request.getHeader(HEADER_TENANT_ID))) {
      String tenantId = request.getHeader(HEADER_TENANT_ID);
      if (StringUtils.isNotBlank(tenantId) && StringUtils.isNumeric(tenantId)) {
        try {
          this.tenantContext.setTenantId(Long.parseLong(tenantId));
        } catch (NumberFormatException e) {
          log.error("Invalid tenant ID format: {}", tenantId, e);
        }
      }
    }
    return true;
  }

  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      @Nullable Exception ex) {
    tenantContext.clear();
  }
}
