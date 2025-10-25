package com.cms.repository.tenant.resource;

import com.cms.model.tenant.resource.TenantResource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantResourceRepository extends JpaRepository<TenantResource, String> {

  Optional<TenantResource> findByTenant_IdAndCode(Long tenantId, String code);

  List<TenantResource> findByTenant_Id(Long tenantId);

  List<TenantResource> findByTenant_IdAndType_Code(Long tenantId, String typeCode);
}
