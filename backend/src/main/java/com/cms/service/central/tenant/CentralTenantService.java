package com.cms.service.central.tenant;

import com.cms.model.dto.central.tenant.TenantCreateDTO;
import com.cms.model.dto.central.tenant.PaginatedTenantVO;
import com.cms.model.dto.central.tenant.TenantVO;
import com.cms.model.dto.central.tenant.TenantStatusVO;
import com.cms.model.dto.central.tenant.TenantUpdateDTO;
import java.util.Optional;

public interface CentralTenantService {
  PaginatedTenantVO<TenantVO> list(int page, int perPage, String search);

  Optional<TenantVO> getById(String id);

  Optional<TenantVO> getByDomain(String domain);

  void create(TenantCreateDTO req);

  void update(String id, TenantUpdateDTO req);

  void delete(String id);

  TenantStatusVO stats();
}
