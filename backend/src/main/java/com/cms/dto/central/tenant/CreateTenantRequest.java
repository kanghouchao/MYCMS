package com.cms.dto.central.tenant;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateTenantRequest {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "domain is required")
    private String domain;

    private String template;
}
