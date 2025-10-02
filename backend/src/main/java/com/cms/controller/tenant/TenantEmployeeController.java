package com.cms.controller.tenant;

import com.cms.dto.tenant.employee.EmployeeCreateRequest;
import com.cms.dto.tenant.employee.EmployeeResponse;
import com.cms.service.tenant.TenantEmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/tenant/employees")
@RequiredArgsConstructor
public class TenantEmployeeController {

  private final TenantEmployeeService tenantEmployeeService;

  @GetMapping
  public ResponseEntity<List<EmployeeResponse>> list() {
    return ResponseEntity.ok(tenantEmployeeService.list());
  }

  @PostMapping
  public ResponseEntity<EmployeeResponse> create(
      @Valid @RequestBody EmployeeCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(tenantEmployeeService.create(request));
  }
}
