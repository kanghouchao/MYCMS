package com.cms.service.central.auth;

import com.cms.dto.auth.Token;

/** Authentication service interface. */
public interface CentralAuthService {

  /**
   * Logs in a user for Central Authentication.
   *
   * @param username the username
   * @param password the password
   * @return the JWT token
   */
  Token login(String username, String password);
}
