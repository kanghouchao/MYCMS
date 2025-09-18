package com.cms.repository.tenant;

import com.cms.model.tenant.security.TenantUser;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantUserRepository extends CrudRepository<TenantUser, String> {
  Optional<TenantUser> findByEmail(String email);

  Optional<TenantUser> findByTenant_IdAndEmail(Long tenantId, String email);
}
