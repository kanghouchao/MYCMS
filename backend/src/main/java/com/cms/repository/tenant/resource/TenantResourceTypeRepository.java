package com.cms.repository.tenant.resource;

import com.cms.model.tenant.resource.TenantResourceType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantResourceTypeRepository extends JpaRepository<TenantResourceType, String> {

  Optional<TenantResourceType> findByTenant_IdAndCode(Long tenantId, String code);

  List<TenantResourceType> findByTenant_IdOrderByNameAsc(Long tenantId);
}
