package com.cms.service.tenant.resource;

import com.cms.config.TenantContext;
import com.cms.dto.resource.CreateResourceRequest;
import com.cms.dto.resource.CreateResourceSchemaRequest;
import com.cms.dto.resource.CreateResourceTypeRequest;
import com.cms.dto.resource.ResourceResponse;
import com.cms.dto.resource.ResourceSchemaResponse;
import com.cms.dto.resource.ResourceTypeResponse;
import com.cms.model.central.tenant.Tenant;
import com.cms.model.tenant.resource.ResourceSchemaStatus;
import com.cms.model.tenant.resource.ResourceSharingScope;
import com.cms.model.tenant.resource.ResourceStatus;
import com.cms.model.tenant.resource.TenantResource;
import com.cms.model.tenant.resource.TenantResourceSchema;
import com.cms.model.tenant.resource.TenantResourceType;
import com.cms.repository.central.TenantRepository;
import com.cms.repository.tenant.resource.TenantResourceRepository;
import com.cms.repository.tenant.resource.TenantResourceSchemaRepository;
import com.cms.repository.tenant.resource.TenantResourceTypeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantResourceServiceImpl implements TenantResourceService {

  private final TenantResourceTypeRepository typeRepository;
  private final TenantResourceSchemaRepository schemaRepository;
  private final TenantResourceRepository resourceRepository;
  private final TenantRepository tenantRepository;
  private final TenantContext tenantContext;
  private final ObjectMapper objectMapper;

  @Override
  public ResourceTypeResponse createType(CreateResourceTypeRequest request) {
    Tenant tenant = requireTenant();
    typeRepository
        .findByTenant_IdAndCode(tenant.getId(), request.code())
        .ifPresent(
            existing -> {
              throw new IllegalStateException("Resource type with code already exists for tenant");
            });

    TenantResourceType entity = new TenantResourceType();
    entity.setTenant(tenant);
    entity.setCode(request.code());
    entity.setName(request.name());
    entity.setDescription(request.description());
    entity.setMetadata(ensureObjectNode(request.metadata()));

    TenantResourceType saved = typeRepository.save(entity);
    return toTypeResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ResourceTypeResponse> listTypes() {
    Long tenantId = requireTenantId();
    return typeRepository.findByTenant_IdOrderByNameAsc(tenantId).stream()
        .map(this::toTypeResponse)
        .toList();
  }

  @Override
  public ResourceSchemaResponse createSchema(CreateResourceSchemaRequest request) {
    Tenant tenant = requireTenant();
    TenantResourceType type =
        typeRepository
            .findByTenant_IdAndCode(tenant.getId(), request.typeCode())
            .orElseThrow(() -> new IllegalArgumentException("Resource type not found"));

    schemaRepository
        .findByType_IdAndVersion(type.getId(), request.version())
        .ifPresent(
            s -> {
              throw new IllegalStateException("Schema version already exists for this type");
            });

    TenantResourceSchema schema = new TenantResourceSchema();
    schema.setTenant(tenant);
    schema.setType(type);
    schema.setVersion(request.version());
    schema.setName(request.name());
    schema.setStatus(Optional.ofNullable(request.status()).orElse(ResourceSchemaStatus.DRAFT));
    schema.setDefinition(ensureObjectNode(request.definition()));
    schema.setUiSchema(ensureObjectNode(request.uiSchema()));
    schema.setDefaults(ensureObjectNode(request.defaults()));
    schema.setMetadata(ensureObjectNode(request.metadata()));
    if (StringUtils.hasText(request.parentSchemaId())) {
      TenantResourceSchema parent =
          schemaRepository
              .findByTenant_IdAndId(tenant.getId(), request.parentSchemaId())
              .orElseThrow(() -> new IllegalArgumentException("Parent schema not found"));
      schema.setParentSchema(parent);
    }

    TenantResourceSchema saved = schemaRepository.save(schema);
    return toSchemaResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ResourceSchemaResponse> listSchemas(String typeCode) {
    Long tenantId = requireTenantId();
    if (StringUtils.hasText(typeCode)) {
      return schemaRepository
          .findByTenant_IdAndType_CodeOrderByVersionDesc(tenantId, typeCode)
          .stream()
          .map(this::toSchemaResponse)
          .toList();
    }
    return schemaRepository.findByTenant_Id(tenantId).stream().map(this::toSchemaResponse).toList();
  }

  @Override
  public ResourceResponse createResource(CreateResourceRequest request) {
    Tenant tenant = requireTenant();
    TenantResourceType type =
        typeRepository
            .findByTenant_IdAndCode(tenant.getId(), request.typeCode())
            .orElseThrow(() -> new IllegalArgumentException("Resource type not found"));
    TenantResourceSchema schema =
        schemaRepository
            .findByTenant_IdAndId(tenant.getId(), request.schemaId())
            .orElseThrow(() -> new IllegalArgumentException("Resource schema not found"));

    if (!Objects.equals(schema.getType().getId(), type.getId())) {
      throw new IllegalArgumentException("Schema does not belong to the specified type");
    }

    resourceRepository
        .findByTenant_IdAndCode(tenant.getId(), request.code())
        .ifPresent(
            r -> {
              throw new IllegalStateException("Resource code already exists for tenant");
            });

    TenantResource resource = new TenantResource();
    resource.setTenant(tenant);
    resource.setType(type);
    resource.setSchema(schema);
    resource.setSchemaVersion(schema.getVersion());
    resource.setCode(request.code());
    resource.setName(request.name());
    resource.setSummary(request.summary());
    resource.setStatus(Optional.ofNullable(request.status()).orElse(ResourceStatus.DRAFT));
    resource.setSharingScope(
        Optional.ofNullable(request.sharingScope()).orElse(ResourceSharingScope.TENANT_ONLY));
    resource.setAttributes(ensureObjectNode(request.attributes()));
    resource.setExtensions(ensureObjectNode(request.extensions()));
    resource.setMetadata(ensureObjectNode(request.metadata()));

    if (resource.getStatus() == ResourceStatus.PUBLISHED) {
      resource.setPublishedAt(OffsetDateTime.now());
    }

    TenantResource saved = resourceRepository.save(resource);
    return toResourceResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ResourceResponse> listResources(String typeCode) {
    Long tenantId = requireTenantId();
    List<TenantResource> result;
    if (StringUtils.hasText(typeCode)) {
      result = resourceRepository.findByTenant_IdAndType_Code(tenantId, typeCode);
    } else {
      result = resourceRepository.findByTenant_Id(tenantId);
    }
    return result.stream().map(this::toResourceResponse).toList();
  }

  private ResourceTypeResponse toTypeResponse(TenantResourceType entity) {
    return new ResourceTypeResponse(
        entity.getId(),
        entity.getCode(),
        entity.getName(),
        entity.getDescription(),
        entity.getMetadata(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  private ResourceSchemaResponse toSchemaResponse(TenantResourceSchema schema) {
    return new ResourceSchemaResponse(
        schema.getId(),
        schema.getType().getId(),
        schema.getType().getCode(),
        schema.getVersion(),
        schema.getName(),
        schema.getStatus(),
        schema.getDefinition(),
        schema.getUiSchema(),
        schema.getDefaults(),
        schema.getMetadata(),
        schema.getParentSchema() != null ? schema.getParentSchema().getId() : null,
        schema.getCreatedAt(),
        schema.getUpdatedAt());
  }

  private ResourceResponse toResourceResponse(TenantResource resource) {
    return new ResourceResponse(
        resource.getId(),
        resource.getType().getId(),
        resource.getType().getCode(),
        resource.getSchema().getId(),
        resource.getSchemaVersion(),
        resource.getCode(),
        resource.getName(),
        resource.getSummary(),
        resource.getStatus(),
        resource.getSharingScope(),
        resource.getAttributes(),
        resource.getExtensions(),
        resource.getMetadata(),
        resource.getPublishedAt(),
        resource.getCreatedAt(),
        resource.getUpdatedAt());
  }

  private Tenant requireTenant() {
    Long tenantId = requireTenantId();
    return tenantRepository
        .findById(tenantId)
        .orElseThrow(() -> new IllegalStateException("Tenant not found: " + tenantId));
  }

  private Long requireTenantId() {
    String tenantId = tenantContext.getTenantId();
    if (!StringUtils.hasText(tenantId)) {
      throw new IllegalStateException("Tenant context is required");
    }
    try {
      return Long.parseLong(tenantId);
    } catch (NumberFormatException ex) {
      throw new IllegalStateException("Invalid tenant id: " + tenantId);
    }
  }

  private JsonNode ensureObjectNode(JsonNode node) {
    if (node == null || node.isNull()) {
      return objectMapper.createObjectNode();
    }
    if (node.isObject()) {
      return node;
    }
    ObjectNode objectNode = objectMapper.createObjectNode();
    objectNode.set("value", node);
    return objectNode;
  }
}
