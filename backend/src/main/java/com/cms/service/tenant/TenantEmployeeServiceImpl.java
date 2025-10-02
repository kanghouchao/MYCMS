package com.cms.service.tenant;

import com.cms.config.TenantContext;
import com.cms.dto.tenant.employee.EmployeeCreateRequest;
import com.cms.dto.tenant.employee.EmployeeResponse;
import com.cms.model.central.tenant.Tenant;
import com.cms.model.tenant.employee.Employee;
import com.cms.repository.central.TenantRepository;
import com.cms.repository.tenant.EmployeeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@Log4j2
@RequiredArgsConstructor
public class TenantEmployeeServiceImpl implements TenantEmployeeService {

  private final TenantContext tenantContext;
  private final TenantRepository tenantRepository;
  private final EmployeeRepository employeeRepository;

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeResponse> list() {
    Long tenantId = resolveTenantId();
    return employeeRepository.findByTenant_IdOrderByCreatedAtDesc(tenantId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public EmployeeResponse create(EmployeeCreateRequest request) {
    Long tenantId = resolveTenantId();
    Tenant tenant = tenantRepository
        .findById(tenantId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));

    String normalizedEmail = StringUtils.hasText(request.getEmail()) ? request.getEmail().trim() : null;

    if (normalizedEmail != null) {
      employeeRepository
          .findByTenant_IdAndEmailIgnoreCase(tenantId, normalizedEmail)
          .ifPresent(
              existing -> {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Employee with email already exists");
              });
    }

    Employee employee = new Employee();
    employee.setTenant(tenant);
    employee.setName(request.getName());
    employee.setEmail(normalizedEmail);
    employee.setPhone(request.getPhone());
    employee.setPosition(request.getPosition());
    employee.setActive(Boolean.TRUE);

    Employee saved = employeeRepository.save(employee);
    log.debug("Created employee id={} for tenant={}", saved.getId(), tenantId);
    return toResponse(saved);
  }

  private Long resolveTenantId() {
    String tenantIdValue = tenantContext.getTenantId();
    if (!StringUtils.hasText(tenantIdValue)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing tenant context");
    }
    try {
      return Long.valueOf(tenantIdValue);
    } catch (NumberFormatException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid tenant ID context");
    }
  }

  private EmployeeResponse toResponse(Employee employee) {
    return EmployeeResponse.builder()
        .id(employee.getId())
        .name(employee.getName())
        .email(employee.getEmail())
        .phone(employee.getPhone())
        .position(employee.getPosition())
        .active(Boolean.TRUE.equals(employee.getActive()))
        .createdAt(employee.getCreatedAt())
        .updatedAt(employee.getUpdatedAt())
        .build();
  }
}
