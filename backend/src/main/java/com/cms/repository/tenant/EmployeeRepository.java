package com.cms.repository.tenant;

import com.cms.model.tenant.employee.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, String> {

  List<Employee> findByTenant_IdOrderByCreatedAtDesc(Long tenantId);

  Optional<Employee> findByTenant_IdAndEmailIgnoreCase(Long tenantId, String email);
}
