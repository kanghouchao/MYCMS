package com.cms.dto.tenant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TenantRegisterResponse {
  String tenantDomain;
  String tenantName;
  String loginUrl;
}
