package com.cms.service.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cms.config.TenantContext;
import com.cms.dto.tenant.employee.EmployeeCreateRequest;
import com.cms.dto.tenant.employee.EmployeeResponse;
import com.cms.model.central.tenant.Tenant;
import com.cms.model.tenant.employee.Employee;
import com.cms.repository.central.TenantRepository;
import com.cms.repository.tenant.EmployeeRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TenantEmployeeServiceImplTest {

  @Mock private TenantContext tenantContext;
  @Mock private TenantRepository tenantRepository;
  @Mock private EmployeeRepository employeeRepository;

  @InjectMocks private TenantEmployeeServiceImpl service;

  @BeforeEach
  void setup() {
    when(tenantContext.getTenantId()).thenReturn("42");
  }

  @Test
  void listReturnsEmployeesForTenant() {
    Employee employee = new Employee();
    employee.setId("emp-1");
    employee.setName("Alice");
    employee.setEmail("alice@example.com");
    employee.setCreatedAt(OffsetDateTime.now());
    employee.setUpdatedAt(employee.getCreatedAt());

    when(employeeRepository.findByTenant_IdOrderByCreatedAtDesc(42L)).thenReturn(List.of(employee));

    List<EmployeeResponse> responses = service.list();

    assertThat(responses).hasSize(1);
    EmployeeResponse response = responses.get(0);
    assertThat(response.getId()).isEqualTo("emp-1");
    assertThat(response.getName()).isEqualTo("Alice");
    assertThat(response.getEmail()).isEqualTo("alice@example.com");
    verify(employeeRepository).findByTenant_IdOrderByCreatedAtDesc(42L);
  }

  @Test
  void createPersistsEmployeeForTenant() {
    Tenant tenant = new Tenant();
    tenant.setId(42L);
    tenant.setName("Demo Store");
    tenant.setDomain("demo.example.com");

    when(tenantRepository.findById(42L)).thenReturn(Optional.of(tenant));
    when(employeeRepository.findByTenant_IdAndEmailIgnoreCase(42L, "staff@example.com"))
        .thenReturn(Optional.empty());
    when(employeeRepository.save(any(Employee.class)))
        .thenAnswer(
            invocation -> {
              Employee toSave = invocation.getArgument(0, Employee.class);
              toSave.setId("emp-2");
              OffsetDateTime now = OffsetDateTime.now();
              toSave.setCreatedAt(now);
              toSave.setUpdatedAt(now);
              return toSave;
            });

    EmployeeCreateRequest request = new EmployeeCreateRequest();
    request.setName("Staff One");
    request.setEmail("staff@example.com");
    request.setPhone("090-1234-5678");
    request.setPosition("Manager");

    EmployeeResponse response = service.create(request);

    ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
    verify(employeeRepository).save(captor.capture());
    Employee saved = captor.getValue();

    assertThat(saved.getTenant()).isEqualTo(tenant);
    assertThat(saved.getName()).isEqualTo("Staff One");
    assertThat(saved.getPhone()).isEqualTo("090-1234-5678");
    assertThat(saved.getPosition()).isEqualTo("Manager");
    assertThat(response.getId()).isEqualTo("emp-2");
    assertThat(response.getName()).isEqualTo("Staff One");
    assertThat(response.getActive()).isTrue();
  }

  @Test
  void createRejectsDuplicateEmail() {
    Tenant tenant = new Tenant();
    tenant.setId(42L);
    tenant.setName("Demo Store");

    when(tenantRepository.findById(42L)).thenReturn(Optional.of(tenant));
    when(employeeRepository.findByTenant_IdAndEmailIgnoreCase(42L, "dup@example.com"))
        .thenReturn(Optional.of(new Employee()));

    EmployeeCreateRequest request = new EmployeeCreateRequest();
    request.setName("Dup");
    request.setEmail("dup@example.com");

    assertThatThrownBy(() -> service.create(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void listFailsWhenTenantContextMissing() {
    when(tenantContext.getTenantId()).thenReturn(null);

    assertThatThrownBy(() -> service.list())
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Missing tenant context");
  }

  @Test
  void listFailsWhenTenantContextInvalid() {
    when(tenantContext.getTenantId()).thenReturn("abc");

    assertThatThrownBy(() -> service.list())
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Invalid tenant id");
  }
}
