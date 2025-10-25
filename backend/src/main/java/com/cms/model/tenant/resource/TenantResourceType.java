package com.cms.model.tenant.resource;

import com.cms.model.tenant.BaseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_resource_types")
public class TenantResourceType extends BaseEntity {

  @Column(name = "group_id")
  private Long groupId;

  @Column(nullable = false, length = 100)
  private String code;

  @Column(nullable = false, length = 150)
  private String name;

  @Column(length = 500)
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata_json", nullable = false, columnDefinition = "jsonb")
  private JsonNode metadata = JsonNodeFactory.instance.objectNode();

  @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
  private Set<TenantResourceSchema> schemas = new HashSet<>();
}
