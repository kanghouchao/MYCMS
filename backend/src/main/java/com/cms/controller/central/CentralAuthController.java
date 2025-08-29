package com.cms.controller.central;

import com.cms.dto.auth.LoginRequest;
import com.cms.service.central.auth.CentralAuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;

/**
 * Controller for Central Authentication.
 *
 * @author KangHouchao
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class CentralAuthController {

    private final CentralAuthService authService;

    /**
     * Logs in a user for Central Authentication.
     *
     * @param req the login request
     * @return the login response
     */
    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req.getUsername(), req.getPassword()));
    }
}
