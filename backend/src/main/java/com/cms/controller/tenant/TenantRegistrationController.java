package com.cms.controller.tenant;

import com.cms.service.central.tenant.TenantRegistrationService;
import com.cms.service.tenant.TenantAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cms.dto.tenant.TenantRegisterRequest;

@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantRegistrationController {

    private final TenantRegistrationService registrationService;
    private final TenantAuthService tenantAuthService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody TenantRegisterRequest tenantRegisterRequest) {
        Long tenantId = registrationService.validate(tenantRegisterRequest.getToken());
        tenantAuthService.register(tenantId, tenantRegisterRequest);
        registrationService.consume(tenantRegisterRequest.getToken());
        return ResponseEntity.noContent().build();
    }

}
