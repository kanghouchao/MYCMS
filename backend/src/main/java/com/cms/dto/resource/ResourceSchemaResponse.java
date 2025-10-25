package com.cms.dto.resource;

import com.cms.model.tenant.resource.ResourceSchemaStatus;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;

public record ResourceSchemaResponse(
    String id,
    String typeId,
    String typeCode,
    Integer version,
    String name,
    ResourceSchemaStatus status,
    JsonNode definition,
    JsonNode uiSchema,
    JsonNode defaults,
    JsonNode metadata,
    String parentSchemaId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
