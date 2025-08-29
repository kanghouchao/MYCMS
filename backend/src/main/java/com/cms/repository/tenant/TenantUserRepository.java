package com.cms.repository.tenant;

import com.cms.model.tenant.security.TenantUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantUserRepository extends CrudRepository<TenantUser, String> {
    Optional<TenantUser> findByEmail(String email);
}
