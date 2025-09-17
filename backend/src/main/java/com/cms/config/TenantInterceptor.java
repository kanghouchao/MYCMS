package com.cms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantContext tenantContext;

    public TenantInterceptor(TenantContext tenantContext) {
        this.tenantContext = tenantContext;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        String tenantId = request.getHeader("X-Tenant-ID");
        log.debug("Incoming request for tenant: {}", tenantId);
        if (tenantId != null) {
            tenantContext.setTenantId(tenantId);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex) {
        tenantContext.clear();
    }
}
