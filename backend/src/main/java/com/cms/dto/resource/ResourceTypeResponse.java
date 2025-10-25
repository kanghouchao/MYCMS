package com.cms.dto.resource;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;

public record ResourceTypeResponse(
    String id,
    String code,
    String name,
    String description,
    JsonNode metadata,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
