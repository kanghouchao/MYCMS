package com.cms.controller.central;

import com.cms.dto.central.tenant.CreateTenantRequest;
import com.cms.dto.central.tenant.PaginatedResponse;
import com.cms.dto.central.tenant.TenantDto;
import com.cms.dto.central.tenant.TenantStats;
import com.cms.dto.central.tenant.UpdateTenantRequest;
import com.cms.service.central.tenant.CentralTenantService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@RestController
@RequestMapping("/central")
@RequiredArgsConstructor
public class CentralTenantController {

  private final CentralTenantService tenantService;

  @GetMapping("tenants")
  @RolesAllowed("ADMIN")
  public ResponseEntity<PaginatedResponse<TenantDto>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(name = "per_page", defaultValue = "10") int perPage,
      @RequestParam(required = false) String search) {
    return ResponseEntity.ok(tenantService.list(page, perPage, search));
  }

  @GetMapping("tenant/{id}")
  @RolesAllowed("ADMIN")
  public ResponseEntity<TenantDto> getById(@PathVariable String id) {
    return tenantService
        .getById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

    /**
     * Get tenant by domain
     * - Accessible to all (no authentication required)
     * - It is for frontend to get tenant info in middleware
     * @param domain the domain of the tenant
     * @return TenantDto
     */
  @GetMapping(value = "tenant", params = "domain")
  @PermitAll
  public ResponseEntity<TenantDto> getByDomain(@RequestParam String domain) {
    return tenantService
        .getByDomain(domain)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("tenant")
  @RolesAllowed("ADMIN")
  public ResponseEntity<Void> create(@Valid @RequestBody CreateTenantRequest req) {
    tenantService.create(req);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("tenant/{id}")
  @RolesAllowed("ADMIN")
  public ResponseEntity<Void> update(
      @PathVariable String id, @RequestBody UpdateTenantRequest req) {
    tenantService.update(id, req);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("tenant/{id}")
  @RolesAllowed("ADMIN")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    tenantService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("tenants/stats")
  @RolesAllowed("ADMIN")
  public ResponseEntity<TenantStats> stats() {
    return ResponseEntity.ok(tenantService.stats());
  }
}
