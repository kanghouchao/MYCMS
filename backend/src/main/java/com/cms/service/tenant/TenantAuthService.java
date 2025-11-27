package com.cms.service.tenant;

import com.cms.model.dto.auth.Token;
import com.cms.model.dto.tenant.TenantRegisterRequest;
import com.cms.model.entity.central.tenant.Tenant;

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
  Tenant register(Long tenantId, TenantRegisterRequest tenant);
}
