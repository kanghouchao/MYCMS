package com.cms.model.tenant.resource;

import com.cms.model.tenant.BaseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_resources")
public class TenantResource extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", nullable = false)
  private TenantResourceType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schema_id", nullable = false)
  private TenantResourceSchema schema;

  @Column(name = "schema_version", nullable = false)
  private Integer schemaVersion;

  @Column(name = "group_id")
  private Long groupId;

  @Column(nullable = false, length = 100)
  private String code;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(length = 500)
  private String summary;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private ResourceStatus status = ResourceStatus.DRAFT;

  @Enumerated(EnumType.STRING)
  @Column(name = "sharing_scope", nullable = false, length = 32)
  private ResourceSharingScope sharingScope = ResourceSharingScope.TENANT_ONLY;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "attributes_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode attributes = JsonNodeFactory.instance.objectNode();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "extensions_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode extensions = JsonNodeFactory.instance.objectNode();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode metadata = JsonNodeFactory.instance.objectNode();

  @Column(name = "published_at")
  private OffsetDateTime publishedAt;
}
