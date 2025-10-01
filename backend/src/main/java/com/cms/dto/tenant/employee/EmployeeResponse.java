package com.cms.dto.tenant.employee;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
  private String id;
  private String name;
  private String email;
  private String phone;
  private String position;
  private Boolean active;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
