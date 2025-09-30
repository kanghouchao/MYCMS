# Role Permission Matrix

## Purpose
This matrix captures the target responsibilities for the platform (central), tenant, and customer actors so that authentication, authorization, and UX flows can be partitioned consistently.

## Legend
- `Y`: Allowed by default.
- `R`: Allowed with restrictions (see notes).
- `N`: Not permitted.

## Platform (Central) Roles
| Operation domain | Admin | Sales | Ops | Scope / notes |
| --- | --- | --- | --- | --- |
| View tenant directory & details | Y | Y | Y | Includes pagination, search, stats endpoints.
| Create tenant & issue registration token | Y | Y | R | Ops only for emergency reissues with audit trail.
| Update tenant profile (name, domain, email) | Y | R | N | Sales needs approval workflow for domain edits.
| Suspend / reinstate tenant | Y | R | Y | Sales requests go through admin approval.
| Delete tenant | Y | N | R | Ops handles teardown scripts; soft-delete first.
| View tenant usage analytics | Y | Y | R | Ops can access infrastructure metrics only.
| Manage central user accounts | Y | N | N | CRUD on `central_users`, password reset, enable/disable.
| Manage central roles & permissions | Y | N | R | Ops can read-only verify for incident response.
| Manage platform templates & global assets | Y | Y | R | Ops handles deployment packaging.
| Configure billing & subscription plans | Y | Y | N | Sales edits quotes; admin finalizes pricing.
| System configuration (feature flags, integrations) | Y | R | Y | Ops executes deploy-time overrides.
| Access audit logs | Y | R | Y | Sales access limited to tenant they manage.
| Manage observability stacks (logging, metrics, alerts) | R | N | Y | Admin covers policy; ops handles tooling.

## Tenant Roles
| Operation domain | Tenant Admin | Staff | Scope / notes |
| --- | --- | --- | --- |
| Manage tenant profile (branding, domains, metadata) | Y | R | Staff can update non-critical content only.
| Manage tenant user accounts & roles | Y | N | Includes invitations, activation, role assignment.
| Configure content structure (collections, templates, menus) | Y | Y | Admin approves breaking schema changes.
| CRUD site content (pages, posts, media) | Y | Y | Subject to workflow policies configured per role.
| Publish / unpublish content | Y | R | Staff requires approval unless delegated.
| Manage customer segments & memberships | Y | R | Staff limited to read/update within assigned segment.
| Access tenant analytics & reports | Y | R | Staff sees dashboards scoped to their departments.
| Manage commerce settings (catalog, pricing, taxes) | Y | R | Staff can update inventory only.
| Trigger bulk actions (imports, exports, automated jobs) | Y | N | Admin must sign off due to data risk.
| Configure integrations (payment, messaging, apps) | Y | N | Admin handles secrets and callbacks.
| Handle support tickets & customer communications | R | Y | Admin sees escalations; staff handles day-to-day.
| View audit trail for tenant | Y | R | Staff access limited to actions they performed.

## Customer Roles
| Operation domain | Member | Visitor | Scope / notes |
| --- | --- | --- | --- |
| Browse public tenant content | Y | Y | Visitor sees public catalog/pages.
| Access gated/member-only content | Y | N | Requires active membership and email verification.
| Manage personal profile & preferences | Y | N | Includes password resets, notification settings.
| Manage membership status (upgrade, cancel, renew) | Y | N | Subject to tenant-defined workflows.
| Submit orders / bookings / inquiries | Y | R | Visitor can submit inquiry form; payments require membership.
| View order history & receipts | Y | N | Depends on commerce enablement for the tenant.
| Participate in loyalty programs / rewards | Y | N | Maintains point ledger tied to membership.
| Access tenant support portal | Y | R | Visitor limited to anonymous ticket submission.

## Cross-cutting Rules
- Every request must propagate `X-Request-ID`; logging should emit `req=<id>` and `tenant=<scope>` so audit reconstruction stays aligned with this matrix.
- JWTs and sessions must encode `role`, `scope` (`central` / `tenant` / `customer`), `tenant_id` where relevant, and fine-grained permission claims derived from the assignments above.
- Multi-tenant writes require tenant context validation through `TenantInterceptor` plus a permission check; no request should rely solely on client headers.
- Admin-only operations must be wrapped in method-level or domain service checks in addition to endpoint guards to protect background jobs, schedulers, and message listeners.
- Frontend route guards and navigation menus should consume the same permission contracts to avoid divergent UX and back-end enforcement gaps.
- Audit events must capture actor role, tenant scope, resource identifier, and the permission that authorized the action.
