package com.cms.repository.tenant;

import com.cms.model.tenant.security.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantUserRepository extends CrudRepository<User, String> {
    Optional<User> findByEmail(String email);
}
