package com.cms.dto.resource;

import com.cms.model.tenant.resource.ResourceSharingScope;
import com.cms.model.tenant.resource.ResourceStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

public record CreateResourceRequest(
    @NotBlank String typeCode,
    @NotBlank String schemaId,
    @NotBlank String code,
    @NotBlank String name,
    String summary,
    ResourceStatus status,
    ResourceSharingScope sharingScope,
    JsonNode attributes,
    JsonNode extensions,
    JsonNode metadata) {}
