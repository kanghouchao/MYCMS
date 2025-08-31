package com.cms.controller.tenant;

import com.cms.repository.central.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantValidationController {

    private final TenantRepository tenantRepository;

    @GetMapping
    public ResponseEntity<?> validateDomain(@RequestParam String domain) {
        return tenantRepository.findByDomain(domain)
                .map(t -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("tenant_id", t.getId());
                    res.put("tenant_name", t.getName());
                    // template_key currently not modeled; return null or default
                    res.put("template_key", null);
                    return ResponseEntity.ok(res);
                }).orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "not found")));
    }
}
