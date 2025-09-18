package com.cms.dto.central.tenant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TenantStats {
  private final long total;
  private final long active;
  private final long inactive;
  private final long pending;
}
