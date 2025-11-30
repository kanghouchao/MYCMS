package com.cms.config;

import com.cms.config.interceptor.TenantContext;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author kanghouchao
 */
@Aspect
@Component
@Order
@AllArgsConstructor
public class TenantFilterEnable {

  private final EntityManager entityManager;
  private final TenantContext tenantContext;

  @Around(value = "@annotation(com.cms.config.TenantIdAutowired)")
  public Object enableTenantFilterForTenantServiceMethods(ProceedingJoinPoint pjp)
      throws Throwable {
    if (tenantContext.isTenant()) {
      entityManager
          .unwrap(org.hibernate.Session.class)
          .enableFilter("tenantFilter")
          .setParameter("tenantId", tenantContext.getTenantId());
    }
    return pjp.proceed();
  }
}
