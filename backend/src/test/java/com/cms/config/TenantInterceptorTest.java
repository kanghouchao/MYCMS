package com.cms.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantInterceptorTest {

  @Mock
  TenantContext tenantContext;

  @Mock
  HttpServletRequest req;

  @Mock
  HttpServletResponse resp;

  @InjectMocks
  TenantInterceptor interceptor;

  @BeforeEach
  void setUp() {
  }

  @Test
  void preHandle_tenantPath_withTenantRole_andTenantId() throws Exception {
    when(req.getHeader("X-Role")).thenReturn("tenant");
    when(req.getHeader("X-Tenant-ID")).thenReturn("t1");
    when(req.getRequestURI()).thenReturn("/tenant/foo");

    boolean ok = interceptor.preHandle(req, resp, new Object());

    assertTrue(ok);
    verify(tenantContext).setTenantId("t1");
  }

  @Test
  void preHandle_tenantPath_wrongRole_sendsError() throws Exception {
    when(req.getHeader("X-Role")).thenReturn("central");
    when(req.getRequestURI()).thenReturn("/tenant/foo");

    boolean ok = interceptor.preHandle(req, resp, new Object());

    assertFalse(ok);
    verify(resp).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
    verify(tenantContext, never()).setTenantId(anyString());
  }

  @Test
  void preHandle_tenantPath_missingTenantId_sendsError() throws Exception {
    when(req.getHeader("X-Role")).thenReturn("tenant");
    when(req.getHeader("X-Tenant-ID")).thenReturn("");
    when(req.getRequestURI()).thenReturn("/tenant/foo");

    boolean ok = interceptor.preHandle(req, resp, new Object());

    assertFalse(ok);
    verify(resp).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
    verify(tenantContext, never()).setTenantId(anyString());
  }

  @Test
  void preHandle_centralPath_withCentralRole_clearsContext() throws Exception {
    when(req.getHeader("X-Role")).thenReturn("central");
    when(req.getRequestURI()).thenReturn("/central/admin");

    boolean ok = interceptor.preHandle(req, resp, new Object());

    assertTrue(ok);
    verify(tenantContext).clear();
  }

  @Test
  void afterCompletion_always_clearsContext() {
    interceptor.afterCompletion(req, resp, new Object(), null);

    verify(tenantContext).clear();
  }
}
