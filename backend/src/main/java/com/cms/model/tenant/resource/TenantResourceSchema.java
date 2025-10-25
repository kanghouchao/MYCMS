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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_resource_schemas")
public class TenantResourceSchema extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", nullable = false)
  private TenantResourceType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_schema_id")
  private TenantResourceSchema parentSchema;

  @Column(name = "group_id")
  private Long groupId;

  @Column(nullable = false)
  private Integer version;

  @Column(nullable = false, length = 150)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private ResourceSchemaStatus status = ResourceSchemaStatus.DRAFT;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "definition_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode definition = JsonNodeFactory.instance.objectNode();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "ui_schema_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode uiSchema = JsonNodeFactory.instance.objectNode();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "defaults_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode defaults = JsonNodeFactory.instance.objectNode();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode metadata = JsonNodeFactory.instance.objectNode();
}
