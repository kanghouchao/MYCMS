package com.cms.service.tenant;

import com.cms.dto.auth.Token;
import com.cms.dto.tenant.TenantRegisterRequest;

/** Authentication service interface. */
public interface TenantAuthService {

  /**
   * Login a user and generate a JWT token.
   *
   * @param username the username
   * @param password the password
   * @return the JWT token
   */
  Token login(String username, String password);

  /**
   * Save a new tenant user request.
   *
   * @param tenantId the tenant ID
   * @param tenant the tenant user request
   */
  void register(Long tenantId, TenantRegisterRequest tenant);
}
