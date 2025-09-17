package com.cms.repository.central;

import com.cms.model.central.tenant.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends PagingAndSortingRepository<Tenant, Long>, CrudRepository<Tenant, Long> {

    Page<Tenant> findByNameContainingIgnoreCaseOrDomainContainingIgnoreCase(String name, String domain,
            Pageable pageable);

    Optional<Tenant> findByDomain(String domain);
}
