package com.cms.controller.tenant;

import com.cms.dto.resource.CreateResourceRequest;
import com.cms.dto.resource.CreateResourceSchemaRequest;
import com.cms.dto.resource.CreateResourceTypeRequest;
import com.cms.dto.resource.ResourceResponse;
import com.cms.dto.resource.ResourceSchemaResponse;
import com.cms.dto.resource.ResourceTypeResponse;
import com.cms.service.tenant.resource.TenantResourceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenant/resources")
@Validated
@RequiredArgsConstructor
public class TenantResourceController {

  private final TenantResourceService resourceService;

  @PostMapping("/types")
  public ResponseEntity<ResourceTypeResponse> createType(
      @Valid @RequestBody CreateResourceTypeRequest request) {
    ResourceTypeResponse response = resourceService.createType(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/types")
  public List<ResourceTypeResponse> listTypes() {
    return resourceService.listTypes();
  }

  @PostMapping("/schemas")
  public ResponseEntity<ResourceSchemaResponse> createSchema(
      @Valid @RequestBody CreateResourceSchemaRequest request) {
    ResourceSchemaResponse response = resourceService.createSchema(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/schemas")
  public List<ResourceSchemaResponse> listSchemas(
      @RequestParam(value = "typeCode", required = false) String typeCode) {
    return resourceService.listSchemas(typeCode);
  }

  @PostMapping
  public ResponseEntity<ResourceResponse> createResource(
      @Valid @RequestBody CreateResourceRequest request) {
    ResourceResponse response = resourceService.createResource(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public List<ResourceResponse> listResources(
      @RequestParam(value = "typeCode", required = false) String typeCode) {
    return resourceService.listResources(typeCode);
  }
}
