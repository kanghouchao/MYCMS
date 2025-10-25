package com.cms.repository.tenant.resource;

import com.cms.model.tenant.resource.TenantResourceSchema;
import com.cms.model.tenant.resource.TenantResourceType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantResourceSchemaRepository
    extends JpaRepository<TenantResourceSchema, String> {

  Optional<TenantResourceSchema> findByTenant_IdAndId(Long tenantId, String id);

  Optional<TenantResourceSchema> findByType_IdAndVersion(String typeId, Integer version);

  List<TenantResourceSchema> findByTenant_IdAndType_CodeOrderByVersionDesc(
      Long tenantId, String typeCode);

  List<TenantResourceSchema> findByTenant_Id(Long tenantId);

  @Query("select max(s.version) from TenantResourceSchema s where s.type = :type")
  Optional<Integer> findMaxVersion(@Param("type") TenantResourceType type);
}
