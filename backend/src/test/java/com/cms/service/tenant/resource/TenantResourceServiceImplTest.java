package com.cms.service.tenant.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cms.config.TenantContext;
import com.cms.dto.resource.CreateResourceRequest;
import com.cms.dto.resource.CreateResourceSchemaRequest;
import com.cms.dto.resource.CreateResourceTypeRequest;
import com.cms.dto.resource.ResourceResponse;
import com.cms.dto.resource.ResourceSchemaResponse;
import com.cms.dto.resource.ResourceTypeResponse;
import com.cms.model.central.tenant.Tenant;
import com.cms.model.tenant.resource.ResourceSchemaStatus;
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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TenantResourceServiceImplTest {

  private final TenantResourceTypeRepository typeRepository =
      mock(TenantResourceTypeRepository.class);
  private final TenantResourceSchemaRepository schemaRepository =
      mock(TenantResourceSchemaRepository.class);
  private final TenantResourceRepository resourceRepository = mock(TenantResourceRepository.class);
  private final TenantRepository tenantRepository = mock(TenantRepository.class);
  private final TenantContext tenantContext = mock(TenantContext.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  private TenantResourceServiceImpl service;
  private Tenant tenant;

  @BeforeEach
  void setUp() {
    service =
        new TenantResourceServiceImpl(
            typeRepository,
            schemaRepository,
            resourceRepository,
            tenantRepository,
            tenantContext,
            objectMapper);
    when(tenantContext.getTenantId()).thenReturn("1");
    tenant = new Tenant();
    tenant.setId(1L);
    tenant.setName("Acme");
    tenant.setDomain("acme.example");
    tenantRepositoryReturn(tenant);
  }

  @Test
  void createTypePersistsEntity() {
    CreateResourceTypeRequest request =
        new CreateResourceTypeRequest("catalog", "Catalog", "Tenant catalog", null);
    when(typeRepository.findByTenant_IdAndCode(1L, "catalog")).thenReturn(Optional.empty());
    when(typeRepository.save(any()))
        .thenAnswer(
            invocation -> {
              TenantResourceType type = invocation.getArgument(0);
              type.setId("type-1");
              return type;
            });

    ResourceTypeResponse response = service.createType(request);

    assertThat(response.id()).isEqualTo("type-1");
    assertThat(response.code()).isEqualTo("catalog");
    verify(typeRepository).save(any(TenantResourceType.class));
  }

  @Test
  void createTypeRejectsDuplicateCode() {
    TenantResourceType existing = new TenantResourceType();
    when(typeRepository.findByTenant_IdAndCode(1L, "catalog")).thenReturn(Optional.of(existing));

    CreateResourceTypeRequest request =
        new CreateResourceTypeRequest("catalog", "Catalog", null, null);

    assertThatThrownBy(() -> service.createType(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("code already exists");
  }

  @Test
  void createSchemaPersistsEntity() {
    TenantResourceType type = new TenantResourceType();
    type.setId("type-1");
    when(typeRepository.findByTenant_IdAndCode(1L, "catalog")).thenReturn(Optional.of(type));
    when(schemaRepository.findByType_IdAndVersion("type-1", 1)).thenReturn(Optional.empty());
    when(schemaRepository.save(any()))
        .thenAnswer(
            invocation -> {
              TenantResourceSchema schema = invocation.getArgument(0);
              schema.setId("schema-1");
              return schema;
            });

    JsonNode definition = JsonNodeFactory.instance.objectNode().put("title", "schema");
    CreateResourceSchemaRequest request =
        new CreateResourceSchemaRequest(
            "catalog",
            "Default",
            1,
            ResourceSchemaStatus.PUBLISHED,
            definition,
            null,
            null,
            null,
            null);

    ResourceSchemaResponse response = service.createSchema(request);

    assertThat(response.id()).isEqualTo("schema-1");
    assertThat(response.version()).isEqualTo(1);
    verify(schemaRepository).save(any(TenantResourceSchema.class));
  }

  @Test
  void createResourcePersistsEntity() {
    TenantResourceType type = new TenantResourceType();
    type.setId("type-1");
    type.setCode("catalog");
    TenantResourceSchema schema = new TenantResourceSchema();
    schema.setId("schema-1");
    schema.setType(type);
    schema.setTenant(tenant);
    schema.setVersion(1);

    when(typeRepository.findByTenant_IdAndCode(1L, "catalog")).thenReturn(Optional.of(type));
    when(schemaRepository.findByTenant_IdAndId(1L, "schema-1")).thenReturn(Optional.of(schema));
    when(resourceRepository.findByTenant_IdAndCode(1L, "item-1")).thenReturn(Optional.empty());
    when(resourceRepository.save(any()))
        .thenAnswer(
            invocation -> {
              TenantResource resource = invocation.getArgument(0);
              resource.setId("resource-1");
              resource.setCreatedAt(OffsetDateTime.now());
              resource.setUpdatedAt(OffsetDateTime.now());
              return resource;
            });

    CreateResourceRequest request =
        new CreateResourceRequest(
            "catalog",
            "schema-1",
            "item-1",
            "Item",
            "Summary",
            ResourceStatus.PUBLISHED,
            null,
            JsonNodeFactory.instance.objectNode(),
            null,
            null);

    ResourceResponse response = service.createResource(request);

    assertThat(response.id()).isEqualTo("resource-1");
    assertThat(response.schemaVersion()).isEqualTo(1);
    verify(resourceRepository, times(1)).save(any(TenantResource.class));
  }

  private void tenantRepositoryReturn(Tenant tenant) {
    when(tenantRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
  }
}
