package com.cms.dto.central.tenant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTenantRequest {
    private String name;
    private String domain;
    private String template;
}
