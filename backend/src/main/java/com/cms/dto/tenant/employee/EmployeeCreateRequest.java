package com.cms.dto.tenant.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeCreateRequest {

  @NotBlank
  @Size(max = 150)
  private String name;

  @Email
  @NotBlank
  @Size(max = 255)
  private String email;

  @Size(max = 30)
  private String phone;

  @Size(max = 120)
  private String position;
}
