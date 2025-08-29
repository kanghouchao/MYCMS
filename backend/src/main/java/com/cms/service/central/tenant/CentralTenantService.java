package com.cms.service.central.tenant;

import com.cms.dto.central.tenant.*;

import java.util.Optional;

public interface CentralTenantService {
    PaginatedResponse<TenantDto> list(int page, int perPage, String search);

    Optional<TenantDto> getById(String id);

    TenantDto create(CreateTenantRequest req);

    TenantDto update(String id, UpdateTenantRequest req);

    void delete(String id);

    TenantStats stats();
}
