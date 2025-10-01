package com.cms.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class TenantInterceptorTest {

  @Mock TenantContext tenantContext;

  @Mock HttpServletRequest req;

  @Mock HttpServletResponse resp;

  @InjectMocks TenantInterceptor interceptor;

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
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
  void preHandle_tenantPath_withAuthenticatedClaim_setsContext() throws Exception {
    Claims claims = org.mockito.Mockito.mock(Claims.class);
    when(claims.get("tenantId", String.class)).thenReturn("42");
    var auth = new PreAuthenticatedAuthenticationToken("user", "tok", java.util.List.of());
    auth.setDetails(claims);
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(req.getHeader("X-Role")).thenReturn(null);
    when(req.getHeader("X-Tenant-ID")).thenReturn(null);
    when(req.getCookies()).thenReturn(null);
    when(req.getRequestURI()).thenReturn("/tenant/foo");

    boolean ok = interceptor.preHandle(req, resp, new Object());

    assertTrue(ok);
    verify(tenantContext).setTenantId("42");
    verify(resp, never()).sendError(anyInt(), anyString());
  }

  @Test
  void preHandle_tenantPath_whenTenantMismatchBetweenHeaderAndToken() throws Exception {
    Claims claims = org.mockito.Mockito.mock(Claims.class);
    when(claims.get("tenantId", String.class)).thenReturn("100");
    var auth = new PreAuthenticatedAuthenticationToken("user", "tok", java.util.List.of());
    auth.setDetails(claims);
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(req.getHeader("X-Role")).thenReturn("tenant");
    when(req.getHeader("X-Tenant-ID")).thenReturn("101");
    when(req.getRequestURI()).thenReturn("/tenant/foo");

    boolean ok = interceptor.preHandle(req, resp, new Object());

    assertFalse(ok);
    verify(resp).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Tenant scope mismatch"));
    verify(tenantContext, never()).setTenantId(anyString());
  }

  @Test
  void preHandle_tenantLogin_allowsMismatchAndKeepsHeaderTenant() throws Exception {
    Claims claims = org.mockito.Mockito.mock(Claims.class);
    when(claims.get("tenantId", String.class)).thenReturn("100");
    var auth = new PreAuthenticatedAuthenticationToken("user", "tok", java.util.List.of());
    auth.setDetails(claims);
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(req.getHeader("X-Role")).thenReturn("tenant");
    when(req.getHeader("X-Tenant-ID")).thenReturn("101");
    when(req.getRequestURI()).thenReturn("/tenant/login");

    boolean ok = interceptor.preHandle(req, resp, new Object());

    assertTrue(ok);
    verify(resp, never()).sendError(anyInt(), anyString());
    verify(tenantContext).setTenantId("101");
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
