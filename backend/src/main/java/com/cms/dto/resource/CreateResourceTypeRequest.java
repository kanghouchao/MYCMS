package com.cms.dto.resource;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

public record CreateResourceTypeRequest(
    @NotBlank String code, @NotBlank String name, String description, JsonNode metadata) {}
