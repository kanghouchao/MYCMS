package com.cms.dto.resource;

import com.cms.model.tenant.resource.ResourceSchemaStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResourceSchemaRequest(
    @NotBlank String typeCode,
    @NotBlank String name,
    @NotNull Integer version,
    ResourceSchemaStatus status,
    @NotNull JsonNode definition,
    JsonNode uiSchema,
    JsonNode defaults,
    JsonNode metadata,
    String parentSchemaId) {}
