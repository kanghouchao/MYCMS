package com.cms.controller.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cms.dto.tenant.employee.EmployeeCreateRequest;
import com.cms.dto.tenant.employee.EmployeeResponse;
import com.cms.service.tenant.TenantEmployeeService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TenantEmployeeControllerTest {

  @Mock private TenantEmployeeService tenantEmployeeService;

  @InjectMocks private TenantEmployeeController controller;

  @Test
  void listReturnsEmployeeResponses() {
    List<EmployeeResponse> expected =
        List.of(EmployeeResponse.builder().id("1").name("Alice").build());
    when(tenantEmployeeService.list()).thenReturn(expected);

    ResponseEntity<List<EmployeeResponse>> response = controller.list();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsExactlyElementsOf(expected);
    verify(tenantEmployeeService).list();
  }

  @Test
  void createReturnsCreatedResponse() {
    EmployeeCreateRequest request = new EmployeeCreateRequest();
    request.setName("Bob");
    EmployeeResponse created =
        EmployeeResponse.builder().id("emp-123").name("Bob").active(true).build();

    when(tenantEmployeeService.create(any(EmployeeCreateRequest.class))).thenReturn(created);

    ResponseEntity<EmployeeResponse> response = controller.create(request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(created);
    verify(tenantEmployeeService).create(request);
  }
}
