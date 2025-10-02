package com.cms.service.tenant;

import com.cms.dto.tenant.employee.EmployeeCreateRequest;
import com.cms.dto.tenant.employee.EmployeeResponse;
import java.util.List;

public interface TenantEmployeeService {

  List<EmployeeResponse> list();

  EmployeeResponse create(EmployeeCreateRequest request);
}
