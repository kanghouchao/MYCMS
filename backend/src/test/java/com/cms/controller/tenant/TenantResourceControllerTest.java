package com.cms.controller.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cms.dto.resource.CreateResourceRequest;
import com.cms.dto.resource.CreateResourceSchemaRequest;
import com.cms.dto.resource.CreateResourceTypeRequest;
import com.cms.dto.resource.ResourceResponse;
import com.cms.dto.resource.ResourceSchemaResponse;
import com.cms.dto.resource.ResourceTypeResponse;
import com.cms.model.tenant.resource.ResourceSchemaStatus;
import com.cms.model.tenant.resource.ResourceStatus;
import com.cms.service.tenant.resource.TenantResourceService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class TenantResourceControllerTest {

  @Mock private TenantResourceService resourceService;

  @InjectMocks private TenantResourceController controller;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTypeReturnsCreatedResponse() {
    ResourceTypeResponse response =
        new ResourceTypeResponse("type-1", "catalog", "Catalog", null, null, null, null);
    when(resourceService.createType(any())).thenReturn(response);

    CreateResourceTypeRequest request =
        new CreateResourceTypeRequest("catalog", "Catalog", null, null);
    ResponseEntity<ResourceTypeResponse> entity = controller.createType(request);

    assertThat(entity.getStatusCode().value()).isEqualTo(201);
    assertThat(entity.getBody()).isEqualTo(response);
    verify(resourceService).createType(request);
  }

  @Test
  void listTypesDelegatesToService() {
    when(resourceService.listTypes()).thenReturn(List.of());

    List<ResourceTypeResponse> result = controller.listTypes();

    assertThat(result).isEmpty();
    verify(resourceService).listTypes();
  }

  @Test
  void createSchemaReturnsCreatedResponse() {
    ResourceSchemaResponse response =
        new ResourceSchemaResponse(
            "schema-1",
            "type-1",
            "catalog",
            1,
            "Default",
            ResourceSchemaStatus.DRAFT,
            JsonNodeFactory.instance.objectNode(),
            JsonNodeFactory.instance.objectNode(),
            JsonNodeFactory.instance.objectNode(),
            JsonNodeFactory.instance.objectNode(),
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now());
    when(resourceService.createSchema(any())).thenReturn(response);

    CreateResourceSchemaRequest request =
        new CreateResourceSchemaRequest(
            "catalog",
            "Default",
            1,
            ResourceSchemaStatus.DRAFT,
            JsonNodeFactory.instance.objectNode(),
            null,
            null,
            null,
            null);

    ResponseEntity<ResourceSchemaResponse> entity = controller.createSchema(request);

    assertThat(entity.getStatusCode().value()).isEqualTo(201);
    assertThat(entity.getBody()).isEqualTo(response);
    verify(resourceService).createSchema(request);
  }

  @Test
  void createResourceReturnsCreatedResponse() {
    ResourceResponse response =
        new ResourceResponse(
            "resource-1",
            "type-1",
            "catalog",
            "schema-1",
            1,
            "item-1",
            "Item",
            null,
            ResourceStatus.DRAFT,
            null,
            JsonNodeFactory.instance.objectNode(),
            JsonNodeFactory.instance.objectNode(),
            JsonNodeFactory.instance.objectNode(),
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now());
    when(resourceService.createResource(any())).thenReturn(response);

    CreateResourceRequest request =
        new CreateResourceRequest(
            "catalog",
            "schema-1",
            "item-1",
            "Item",
            null,
            ResourceStatus.DRAFT,
            null,
            JsonNodeFactory.instance.objectNode(),
            null,
            null);

    ResponseEntity<ResourceResponse> entity = controller.createResource(request);

    assertThat(entity.getStatusCode().value()).isEqualTo(201);
    assertThat(entity.getBody()).isEqualTo(response);
    verify(resourceService).createResource(request);
  }
}
