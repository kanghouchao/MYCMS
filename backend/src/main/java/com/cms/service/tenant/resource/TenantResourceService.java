package com.cms.service.tenant.resource;

import com.cms.dto.resource.CreateResourceRequest;
import com.cms.dto.resource.CreateResourceSchemaRequest;
import com.cms.dto.resource.CreateResourceTypeRequest;
import com.cms.dto.resource.ResourceResponse;
import com.cms.dto.resource.ResourceSchemaResponse;
import com.cms.dto.resource.ResourceTypeResponse;
import java.util.List;

public interface TenantResourceService {

  ResourceTypeResponse createType(CreateResourceTypeRequest request);

  List<ResourceTypeResponse> listTypes();

  ResourceSchemaResponse createSchema(CreateResourceSchemaRequest request);

  List<ResourceSchemaResponse> listSchemas(String typeCode);

  ResourceResponse createResource(CreateResourceRequest request);

  List<ResourceResponse> listResources(String typeCode);
}
