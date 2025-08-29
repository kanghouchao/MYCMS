package com.cms.controller.central;

import com.cms.dto.central.tenant.*;
import com.cms.service.central.tenant.CentralTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/admin/tenants")
@RequiredArgsConstructor
public class CentralTenantController {

    private final CentralTenantService tenantService;

    @GetMapping
    @RolesAllowed({ "ROLE_ADMIN" })
    public ResponseEntity<PaginatedResponse<TenantDto>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(tenantService.list(page, perPage, search));
    }

    @GetMapping("/{id}")
    @RolesAllowed({ "ROLE_ADMIN" })
    public ResponseEntity<TenantDto> getById(@PathVariable String id) {
        Optional<TenantDto> t = tenantService.getById(id);
        return t.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @RolesAllowed({ "ROLE_ADMIN" })
    public ResponseEntity<TenantDto> create(@Valid @RequestBody CreateTenantRequest req) {
        return ResponseEntity.ok(tenantService.create(req));
    }

    @PutMapping("/{id}")
    @RolesAllowed({ "ROLE_ADMIN" })
    public ResponseEntity<TenantDto> update(@PathVariable String id, @RequestBody UpdateTenantRequest req) {
        return ResponseEntity.ok(tenantService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({ "ROLE_ADMIN" })
    public ResponseEntity<?> delete(@PathVariable String id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @RolesAllowed({ "ROLE_ADMIN" })
    public ResponseEntity<TenantStats> stats() {
        return ResponseEntity.ok(tenantService.stats());
    }
}
