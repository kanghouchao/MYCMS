package com.cms.repository.central;

import com.cms.model.central.security.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CentralUserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
