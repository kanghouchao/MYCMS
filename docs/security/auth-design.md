# Authentication & Authorization Blueprint

## Goals
- Support distinct login flows for platform, tenant, and customer actors with consistent token semantics.
- Deliver fine-grained authorization that aligns with the role matrix and eliminates reliance on mutable client headers.
- Preserve existing hot paths (`/central/login`, `/tenant/login`, middleware cookies) while introducing incremental improvements.

## Current Snapshot
- `JwtAuthenticationFilter` trusts claims but only gates issuer vs. path; authorities come straight from the token with no tenant scoping.
- `TenantInterceptor` reads `X-Role` / `X-Tenant-ID` from headers or cookies to set thread-local context; missing validation against authenticated principal.
- Spring Security allows every request (`.authorizeHttpRequests().anyRequest().permitAll()`), depending on method-level annotations that do not exist for most controllers.
- Frontend middleware assigns `x-mw-role` based on host name but cannot differentiate staff vs. admin or customer personas.
- Redis blacklisting only runs for central logout, leaving tenant JWTs unchecked.

## Target Architecture
### Entry Points
| Actor | Entry path | Auth mechanism | Primary storage |
| --- | --- | --- | --- |
| Platform (central) | `/central/login` | Username/password → Spring AuthenticationManager | `central_users` + `central_roles` |
| Tenant workforce | `/tenant/login` | Username/password scoped by tenant | `t_users` + `t_roles` |
| Customer | `/customer/login` (new) | Email/password or social login | New customer tables per tenant |
| Service-to-service | `/internal/*` (future) | mTLS + signed service tokens | Secrets manager |

### Token Contract
- JWT header: `kid` referencing active signing key (managed via `app.jwt.secret` rotation plan).
- Claims for all actors:
  - `sub`: principal identifier (username, email, or service ID).
  - `aud`: `central`, `tenant`, or `customer`.
  - `iss`: flow-specific issuer (`CentralAuth`, `TenantAuth`, `CustomerAuth`).
  - `exp`, `iat`, `jti` for revocation bookkeeping.
  - `tenant_id`: required for tenant staff & customers; `null` for central.
  - `role_ids`: canonical role IDs (UUID/long) to re-hydrate authorities.
  - `permissions`: optional flattened list for edge caches; must be revalidated server-side.
  - `request_id`: optional but enables log stitching without relying on headers.
- Tokens should be short-lived (15 min) with refresh tokens stored server-side (Redis or database) per audience.

### Authentication Flow Changes
1. **Central login**
   - After `AuthenticationManager` success, fetch roles + permissions and sign JWT with above claims.
   - Store refresh token with scope `central` in Redis (`refresh:central:{userId}:{jti}`).
   - Response returns access token + refresh token expiry.
2. **Tenant login**
   - Augment repository queries to require `tenant_id` in addition to email; ensures `TenantAdmin` can not cross tenants.
   - Generate JWT with `aud=tenant`, `tenant_id`, and resolved permission set derived from `t_roles` join tables.
3. **Customer login**
   - Introduce new authentication service backed by `tenant_customers` table.
   - Support both password and passwordless (email magic link) in later iteration; initial design uses password.
4. **Refresh & logout**
   - New endpoints `/central/token/refresh`, `/tenant/token/refresh`, `/customer/token/refresh` verifying stored refresh tokens.
   - Logout endpoints blacklist current `jti` and delete refresh entry.

### Authorization Checks
- Update `SecurityConfig` to require authentication by default, grouping matchers:
  - Permit `POST /central/login`, `/tenant/login`, `/tenant/register`, `/customer/login`, health probes.
  - Protect `/central/**` with `hasAuthority('CENTRAL:*')`, etc.
  - Protect `/tenant/**` with `hasAuthority('TENANT:*')` plus tenant context check.
  - Introduce `/customer/**` area guarded by `hasAuthority('CUSTOMER:*')`.
- Replace raw `@RolesAllowed` with composed annotations mapping to permission constants that align with the matrix (e.g. `@RequiresPermission("central.tenants.read")`).
- In service layer, validate tenant context matches authenticated principal (`TenantContext.getTenantId()` equals JWT `tenant_id`).
- Extend `TenantInterceptor` to read tenant ID and role from `SecurityContext` instead of trusting cookies/headers; fallback to cookies only for unauthenticated SSR rendering.
- Inject `X-Request-ID` and actor metadata into MDC to ensure logging shows `req`, `tenant`, and `actor`.

### Frontend & Middleware Alignment
- Middleware should derive actor persona from decoded JWT stored in `token` cookie instead of hostname-only heuristics.
- Store structured session cookie (signed) containing `{ audience, tenant_id, roles }` for SSR guards.
- Navigation guards read permission map from a `/me` endpoint for each audience:
  - `/central/me`: returns admin profile + permissions.
  - `/tenant/me`: returns staff profile + tenant scopes.
  - `/customer/me`: returns membership state, entitlements.
- Ensure `apiClient` attaches `X-Role` and `X-Tenant-ID` based on decoded token claims; reject stale cookies.

### Auditing & Observability
- Emit structured audit events on login/logout, permission changes, bulk actions, and admin overrides.
- Include `jti`, `tenant_id`, `aud`, and top-level permission used.
- Funnel audit events to dedicated Kafka topic or Redis stream for later ingestion.

### Backward Compatibility & Migration Strategy
- Phase 1: keep existing `/central` and `/tenant` login endpoints but augment responses with refresh token and new claims.
- Phase 2: backend enforces authentication on protected routes; frontend updated in tandem to call `/me` to refresh context.
- Phase 3: deprecate reliance on middleware-set `x-mw-role` for API authorization; keep cookie for SSR theming only.
- Provide feature flags to toggle strict authorization per module to allow incremental rollout.
