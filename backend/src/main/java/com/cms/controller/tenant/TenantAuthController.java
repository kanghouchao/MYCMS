package com.cms.controller.tenant;

import com.cms.dto.auth.LoginRequest;
import com.cms.dto.auth.LoginResponse;
import com.cms.dto.auth.Token;
import com.cms.security.TokenIntrospector;
import com.cms.service.tenant.TenantAuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantAuthController {

  private final TenantAuthService authService;
  private final TokenIntrospector tokenIntrospector;

  @PostMapping("/login")
  @PermitAll
  public ResponseEntity<LoginResponse> login(
      @Valid @RequestBody LoginRequest req, jakarta.servlet.http.HttpServletRequest request) {
    var tokenDetails = tokenIntrospector.introspect(request);
    if (tokenDetails.isPresent()) {
      return ResponseEntity.ok(new LoginResponse(tokenDetails.get().asToken(), "tenant", "/central/dashboard/central"));
    }
    Token issued = authService.login(req.getUsername(), req.getPassword());
    return ResponseEntity.ok(new LoginResponse(issued, "tenant", "/"));
  }

}
