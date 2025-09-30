package com.cms.constants;

/** Centralised header and cookie key definitions for request context propagation. */
public final class RequestContextKeys {

  private RequestContextKeys() {}

  public static final String HEADER_ROLE = "X-Role";
  public static final String HEADER_TENANT_ID = "X-Tenant-ID";

  public static final String COOKIE_ROLE = "x-mw-role";
  public static final String COOKIE_TENANT_ID = "x-mw-tenant-id";
  public static final String COOKIE_TENANT_NAME = "x-mw-tenant-name";
  public static final String COOKIE_TENANT_TEMPLATE = "x-mw-tenant-template";
}
