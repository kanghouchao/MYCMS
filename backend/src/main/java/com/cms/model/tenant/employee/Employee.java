package com.cms.model.tenant.employee;

import com.cms.model.tenant.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "t_employees",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_t_employees_tenant_email",
          columnNames = {"tenant_id", "email"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {

  @Column(nullable = false, length = 150)
  private String name;

  @Column(length = 255)
  private String email;

  @Column(length = 30)
  private String phone;

  @Column(length = 120)
  private String position;

  @Column(nullable = false)
  private Boolean active = Boolean.TRUE;
}
