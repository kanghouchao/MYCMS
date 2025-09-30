# Role System Implementation Roadmap

## Guiding Principles
- Ship iteratively with isolated pull requests per subsystem to ease review.
- Maintain backwards compatibility until both backend and frontend toggles are in place.
- Capture test evidence (`make test service=backend`, `npm test -- --coverage`) and publish reports for every milestone.

## Phase 0 – Foundations
1. **Design sign-off**
   - Review `docs/security/role-matrix.md`, `auth-design.md`, `data-model-plan.md` with backend, frontend, product stakeholders.
   - Confirm naming conventions for roles and permissions.
2. **Infrastructure prep**
   - Add Liquibase placeholder changelog for new tables (empty change sets guarded by feature flag).
   - Enable secret storage for new JWT signing keys if rotation is required.

## Phase 1 – Platform & Tenant Hardening
### Backend
- Implement permission catalogue entities and repositories; seed central/tenant base roles (Liquibase change set `central-003-role-catalogue.yaml`).
- Update `SecurityConfig` to require authentication and wire new `AuthorizationService` for permission checks.
- Refactor `TenantInterceptor` + `JwtAuthenticationFilter` to source tenant context from authenticated principal.
- Add `/central/me` and `/tenant/me` endpoints returning role + permission payloads.
- Introduce refresh-token endpoints and Redis storage helpers.

### Frontend
- Decode JWT client-side to drive nav/route guards; update `AuthContext` to fetch `/me` on load.
- Replace hostname-only middleware logic with token-driven persona detection; maintain SSR cookies for template selection.
- Update central tenant management pages to handle permission-driven UI states (button disabling, etc.).

### Data & Ops
- Backfill seed data: central admin, sales, ops roles; tenant admin/staff roles.
- Implement migration validation script verifying role/permission counts post-deploy.

### Testing
- Expand backend unit tests for `AuthorizationService`, token refresh, and updated interceptors.
- Add integration test hitting `/central/tenants` with unauthorized token to assert 403.
- Frontend: add Jest/RTL coverage for nav guards and `apiClient` header propagation.

## Phase 2 – Customer Enablement
### Backend
- Create `tenant_customer*` tables and repositories.
- Build `CustomerAuthService` with login/register/reset endpoints and JWT issuance.
- Add customer-facing `/customer/me` and membership management APIs.
- Extend audit logging for customer actions.

### Frontend
- Introduce customer login/registration pages; fetch membership state for gated content.
- Update tenant templates to respect membership entitlements (SSR + CSR).

### Data & Ops
- Seed default customer roles (`customer.visitor`, `customer.member`).
- Configure rate limiting for customer authentication endpoints.

### Testing
- Contract tests between Next.js middleware and backend tenant validation for customer context cookies.
- Add E2E scenario (Playwright/Cypress) covering customer login, content access, logout.

## Phase 3 – Advanced Controls & Tooling
- Implement `policy_assignment` APIs for granular resource permissions.
- Build admin UI for permission management and audit log browsing.
- Integrate monitoring dashboards (request failure rate by audience, token refresh success, etc.).
- Roll out feature flags to enforce strict permissions per module; remove legacy code paths once adoption hits 100%.

## Follow-up Issues (suggested titles)
1. `feat(security): introduce role & permission catalogue tables`
2. `feat(auth): enforce jwt-based authorization for central endpoints`
3. `feat(frontend): add role-aware navigation and guard hooks`
4. `feat(auth): add refresh token flow for central and tenant audiences`
5. `feat(data): scaffold customer authentication tables and services`
6. `feat(frontend): deliver customer membership experience`
7. `feat(observability): expand audit logging for permission changes`
8. `feat(platform): ship granular policy assignment APIs`

## Acceptance Checklist
- ✅ All documents reviewed and version-controlled under `docs/security/`.
- ✅ Liquibase migrations merged with idempotent preconditions.
- ✅ Automated tests updated to cover new auth flows (>70% coverage remains satisfied).
- ✅ Monitoring and alerting configured for authentication failures and permission change events.
- ✅ Rollout plan approved with checkpoints for rollback.
