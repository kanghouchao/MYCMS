package com.cms.dto.resource;

import com.cms.model.tenant.resource.ResourceSharingScope;
import com.cms.model.tenant.resource.ResourceStatus;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;

public record ResourceResponse(
    String id,
    String typeId,
    String typeCode,
    String schemaId,
    Integer schemaVersion,
    String code,
    String name,
    String summary,
    ResourceStatus status,
    ResourceSharingScope sharingScope,
    JsonNode attributes,
    JsonNode extensions,
    JsonNode metadata,
    OffsetDateTime publishedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
