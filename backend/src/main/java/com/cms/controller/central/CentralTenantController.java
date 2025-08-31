package com.cms.controller.central;

import com.cms.dto.central.tenant.*;
import com.cms.service.central.tenant.CentralTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/central/tenants")
@RequiredArgsConstructor
public class CentralTenantController {

    private final CentralTenantService tenantService;

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<PaginatedResponse<TenantDto>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(tenantService.list(page, perPage, search));
    }

    @GetMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<TenantDto> getById(@PathVariable String id) {
        Optional<TenantDto> t = tenantService.getById(id);
        return t.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateTenantRequest req) {
        tenantService.create(req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateTenantRequest req) {
        tenantService.update(id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @RolesAllowed("ADMIN")
    public ResponseEntity<TenantStats> stats() {
        return ResponseEntity.ok(tenantService.stats());
    }
}
