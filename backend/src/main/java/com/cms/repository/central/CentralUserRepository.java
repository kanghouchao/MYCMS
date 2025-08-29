package com.cms.repository.central;

import com.cms.model.central.security.CentralUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CentralUserRepository extends CrudRepository<CentralUser, Long> {
    Optional<CentralUser> findByUsername(String username);
}
