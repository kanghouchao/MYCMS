package com.cms.controller.tenant;

import com.cms.dto.auth.LoginRequest;
import com.cms.dto.auth.LoginResponse;
import com.cms.service.tenant.TenantAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class TenantAuthController {

    private final TenantAuthService authService;

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse body = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(body);
    }
}
