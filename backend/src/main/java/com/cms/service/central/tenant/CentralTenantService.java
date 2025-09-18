package com.cms.service.central.tenant;

import com.cms.dto.central.tenant.CreateTenantRequest;
import com.cms.dto.central.tenant.PaginatedResponse;
import com.cms.dto.central.tenant.TenantDto;
import com.cms.dto.central.tenant.TenantStats;
import com.cms.dto.central.tenant.UpdateTenantRequest;
import java.util.Optional;

public interface CentralTenantService {
  PaginatedResponse<TenantDto> list(int page, int perPage, String search);

  Optional<TenantDto> getById(String id);

  Optional<TenantDto> getByDomain(String domain);

  void create(CreateTenantRequest req);

  void update(String id, UpdateTenantRequest req);

  void delete(String id);

  TenantStats stats();
}
