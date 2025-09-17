package com.cms.config;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TenantContext {
    private final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public String getTenantId() {
        return CURRENT_TENANT.get();
    }

    public boolean isTenant() {
        return StringUtils.hasText(getTenantId());
    }

    public void clear() {
        CURRENT_TENANT.remove();
    }
}
