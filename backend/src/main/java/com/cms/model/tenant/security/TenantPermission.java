package com.cms.model.tenant.security;

import com.cms.model.tenant.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "t_permissions",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_t_permissions_tenant_name",
          columnNames = {"tenant_id", "name"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantPermission extends BaseEntity {

  @Column(nullable = false, length = 150)
  private String name;

  @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
  private Set<TenantRole> roles = new HashSet<>();
}
