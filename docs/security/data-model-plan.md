# Data Model & Migration Plan

## Current Inventory
- **Central**: `central_users`, `central_roles`, `central_permissions`, link tables `central_user_roles` and `central_role_permissions`.
- **Tenant**: `t_users`, `t_roles`, `t_permissions`, `t_user_roles`, `t_role_permissions`; each row carries `tenant_id` but no uniqueness on permissions across tenants.
- **Miscellaneous**: No dedicated customer tables; pages (`t_pages`) reference tenants via foreign key.

Pain points:
- Role names are free-form strings, making it difficult to enforce canonical personas defined in the role matrix.
- Permissions are not classified by domain (content, analytics, billing), complicating policy evaluation.
- No place to represent customer memberships, loyalty tiers, or policy overrides per tenant.
- Liquibase changelog seeds inconsistent values (`AMIND` typo) and lacks initial tenant role seeding.

## Target Entities
### Shared Concepts
- `permission` (global catalogue)
  - Columns: `id` (bigint), `code` (`central.tenants.read`), `description`, `audience` (`CENTRAL|TENANT|CUSTOMER`).
- `role` (global definitions)
  - Columns: `id`, `code` (`central.admin`, `tenant.staff`, `customer.member`), `audience`, `display_name`, `description`, `mutable` (flag for custom roles).
- `role_permission` (many-to-many between roles and permissions).

### Platform (Central)
- `central_user`
  - Existing table extended with `email`, `display_name`, `last_login_at`.
- `central_user_role`
  - Moves to referencing global `role_id` with constraint `audience = 'CENTRAL'`.
- `central_user_tenant_assignment`
  - New table mapping sales/ops users to allowed tenant IDs for scoped analytics.

### Tenant Workforce
- `tenant_user`
  - Add `username` (optional), `phone`, `last_login_at`, `status` (`ACTIVE|SUSPENDED`).
- `tenant_user_role`
  - Adds `role_id` referencing global roles; keep `tenant_id` for multi-tenancy.
- `tenant_role_override`
  - Allows tenants to define custom roles derived from base templates; fields: `id`, `tenant_id`, `base_role_id`, `code`, `display_name`, `description`.
- `tenant_permission_override`
  - Optional expansion for tenant-specific permission toggles (feature flags per tenant).

### Customer Layer
- `tenant_customer`
  - `id` (UUID), `tenant_id`, `email`, `password_hash`, `status`, `verified_at`, `created_at`, `updated_at`.
- `tenant_membership`
  - `id`, `tenant_customer_id`, `tier_code`, `started_at`, `expires_at`, `status`.
- `tenant_customer_role`
  - Link to global roles for `CUSTOMER` audience (e.g. visitor vs. member) supporting entitlements.
- `tenant_customer_profile`
  - Optional JSONB column for preferences, addresses.

### Audit & Policy Tables
- `policy_assignment`
  - Generic table to capture dynamic permissions (e.g., staff granted access to a content collection). Columns: `id`, `audience`, `actor_id`, `resource_type`, `resource_id`, `permission_code`, `scope` JSON, timestamps.
- `permission_change_log`
  - Append-only log to track who modified role-permission mappings.

## ER Overview (textual)
```
role (id) ──< role_permission >── permission (id)
  │                         │
  │                         └── scoped by `audience`
  ├──< central_user_role >── central_user (id)
  ├──< tenant_user_role >── tenant_user (id, tenant_id)
  └──< tenant_customer_role >── tenant_customer (id, tenant_id)

tenant_user (tenant_id) ──< tenant_user_role >── role (TENANT)
tenant_customer (tenant_id) ──< tenant_membership >── membership tiers
policy_assignment ties {audience, actor_id} to fine-grained resource permissions.
```

## Migration Strategy
1. **Introduce global catalog tables** (`permission`, `role`, `role_permission`). Backfill with existing central and tenant roles/permissions.
2. **Migrate central data**
   - Create temp mapping table `central_role_legacy_map` to map legacy IDs to new global ones.
   - Update `central_user_roles` to reference new `role_id` and drop old `central_roles` table once data migrates.
3. **Migrate tenant data**
   - Seed base tenant roles (`tenant.admin`, `tenant.staff`) and map existing `t_roles` entries.
   - Add `role_id` column to `t_user_roles`, populate via join, then drop legacy join table columns.
4. **Bootstrap customers**
   - Add `tenant_customer*` tables with foreign keys to `central_tenants`.
   - No data migration required initially; provide seed script to create default `customer.visitor` and `customer.member` roles.
5. **Cleanup & constraints**
   - Enforce uniqueness: `UNIQUE (audience, code)` on `role`, `permission`.
   - Add composite index `idx_tenant_user_role_tenant_role` on `(tenant_id, role_id)`.
   - Remove typo `AMIND` record; replace with `central.tenants.manage`.
6. **Versioning & rollout**
   - Wrap destructive steps in Liquibase change sets with preconditions checking for existing columns / data state.
   - Provide backward-compatible database views (`central_roles_v_legacy`, `t_roles_v_legacy`) during transition.

## Data Governance Considerations
- Use database triggers or application-level hooks to populate `permission_change_log` for auditability.
- Maintain reference data in code (`.yml` or JSON) to seed permissions/roles during deployment.
- Document tenant-specific customization boundaries to prevent escalations (e.g., staff cannot assign themselves admin role if tenant disables override).
- Plan nightly job to detect orphaned assignments (role references with missing permission definitions) and alert ops.
