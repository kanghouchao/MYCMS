package com.cms.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

class WebMvcConfigTest {

  @Test
  void addsTenantInterceptorToRegistry() {
    TenantInterceptor interceptor = mock(TenantInterceptor.class);
    InterceptorRegistry registry = mock(InterceptorRegistry.class);

    WebMvcConfig config = new WebMvcConfig(interceptor);
    config.addInterceptors(registry);

    verify(registry).addInterceptor(interceptor);
  }
}
