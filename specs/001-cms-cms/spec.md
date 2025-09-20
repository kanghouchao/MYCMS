# Feature Specification: Multi-tenant CMS for small Japanese web/agile teams

**Feature Branch**: `001-cms-cms`  
**Created**: 2025-09-20  
**Status**: Draft  
**Input**: User description: "I work at a small Japanese company that operates ~10 client websites
and several scattered manual attendance and workflow systems. The technology is legacy and
hard to update. I want to create a CMS that can provide those features, multi-tenant so it
serves multiple clients, and make it adoptable by my colleagues. Tenant creation and login
are already implemented; continue development to deliver a usable system colleagues will adopt."

## Execution Flow (main)
```
1. Parse user description from Input
   → If empty: ERROR "No feature description provided"
2. Extract key concepts from description
   → Identify: actors (tenant admin, editor, reviewer), actions (create tenant, create site,
     manage pages, attendance check-in, start workflow), data (tenant, users, pages, records),
     constraints (legacy systems, adoption barriers)
3. For each unclear aspect:
   → Mark with [NEEDS CLARIFICATION: specific question]
4. Fill User Scenarios & Testing section
   → If no clear user flow: ERROR "Cannot determine user scenarios"
5. Generate Functional Requirements (testable)
6. Identify Key Entities and relationships
7. Run Review Checklist
   → If any [NEEDS CLARIFICATION]: WARN "Spec has uncertainties"
   → If implementation details found: ERROR "Remove tech details"
8. Return: SUCCESS (spec ready for planning)
```

---

## Quick Guidelines

- Focus on WHAT users need and WHY
- Avoid HOW to implement (no low-level tech detail in this spec)
- When ambiguous, mark with [NEEDS CLARIFICATION]

### Section Requirements

- Mandatory sections: User Scenarios, Requirements, Key Entities, Review Checklist
- Optional sections: Integration notes, Migration strategy, Performance targets

### For this feature

- This specification prioritizes rapid adoption: prioritize features that enable colleagues
  to replace legacy flows (site content editing, attendance capture, and a basic approval
  workflow). Prefer an incremental rollout: site content first, then workflows/attendance
  integration.

### Developer context & constraints

- Developer is a student at a Japanese vocational school; GitHub AI assistant usage is
  available under a free tier, while GitHub Pro/paid features have limited quota.
- Preference: use GitHub Flow for development (branch → PR → review → merge to master).
- Constraint: AI usage should be economical — favor offline/locally-run tools, small
  automated scripts, and batching AI calls where possible to preserve quota.


## User Scenarios & Testing (mandatory)

### Primary User Story
As a tenant administrator, I can create and configure a client site, invite colleagues,
author pages and publish them so the client's public website is up-to-date without
involving engineering operations.

### Acceptance Scenarios
1. Given an authenticated tenant admin, when they create a site and add a page, then
   the page is saved as draft and can be published to the tenant site URL.
2. Given a user submits an attendance entry, when it is approved by the manager workflow,
   then the attendance record is persisted and visible in tenant reports.

### Edge Cases
- Handling concurrent edits to a page (last-write vs edit-locking decision)
- Offline or flaky networks when employees submit attendance
- Importing legacy content or attendance logs from existing systems

## Requirements (mandatory)

### Functional Requirements
- FR-001: Multi-tenant isolation — System MUST isolate tenant data and templates so no
  tenant can access another tenant's data.
- FR-002: Tenant management — System MUST allow creation, update, and soft-deletion of
  tenants; initial tenant creation and login are implemented, the system MUST provide
  tenant configuration UI (branding, domain, template selection).
- FR-003: User roles — System MUST support tenant-level roles: TenantAdmin, Editor,
  Reviewer; access control MUST be enforced server-side.
- FR-004: Content management — System MUST provide CRUD for Pages and Assets (upload),
  with draft/publish workflow and page version history.
- FR-005: Attendance capture — System MUST accept simple attendance events (check-in,
  check-out) and store them as AttendanceRecord entities; records MUST be queryable per
  tenant and by date range.
- FR-006: Workflow approvals — System MUST provide a simple approval workflow (submit →
  manager review → approve/reject) that can be attached to attendance or other forms.
- FR-007: Legacy integration & import — System SHOULD provide import utilities or
  connectors to ingest existing site content and attendance logs (CSV/JSON) from
  legacy systems. If integration is required, mark as [NEEDS CLARIFICATION: source API].
- FR-008: Observability & errors — System MUST log errors with tenant context and expose
  an admin health endpoint; audits for security events MUST be recorded.
- FR-009: Usability/Adoption — The UI flows for tenant admins and editors MUST be simple
  (<= 3 steps for common tasks like editing & publishing a page) and include onboarding
  docs or quickstart within the app.

### Non-functional Requirements
- NFR-001: Security — No secrets in source; perform server-side validation and auth checks.
- NFR-002: Performance — Typical page publish path SHOULD complete under 2s in local
  dev; we will refine targets after measurement.
- NFR-003: Backward compatibility — Breaking changes to tenant data MUST be accompanied
  by a migration plan and version bump.

## Key Entities

- Tenant: id, name, domain(s), template_key, settings
- User: id, tenant_id, email, role, last_login
- Site/Page: id, tenant_id, title, slug, content (rich text), status (draft/published),
  author_id, versions
- Asset: id, tenant_id, path, content_type, uploaded_by
- AttendanceRecord: id, tenant_id, user_id, check_in, check_out, status, metadata
- WorkflowInstance: id, tenant_id, type, state, payload, created_by

## Integration Notes

- Cookie contract: frontend middleware sets `x-mw-role`, `x-mw-tenant-template`,
  `x-mw-tenant-id`, `x-mw-tenant-name`. Server components MUST validate these cookies.
- Use import tasks for migrating legacy site content and attendance logs; CSV import
  should be a supported first step.

## Review & Acceptance Checklist

### Content Quality
- [ ] No implementation details leaked into the spec
- [ ] Focused on user value and adoption
- [ ] All mandatory sections completed

### Requirements Completeness
- [ ] No [NEEDS CLARIFICATION] markers remain
- [ ] Requirements are testable and have acceptance criteria
- [ ] Success criteria: at least one tenant can create/publish a page and submit an
  attendance record through the workflow

### Adoption Criteria
- [ ] Onboarding guide and quickstart created for tenant admins
- [ ] One internal colleague (non-developer) can publish a page after following docs

## Execution Status

- [x] User description parsed
- [x] Key concepts extracted (multi-tenant, CMS pages, attendance, workflows)
- [ ] Ambiguities marked (none required yet)  
- [ ] User scenarios defined (primary ones present)
- [ ] Requirements generated
- [ ] Entities identified
- [ ] Review checklist pending

---

If this spec looks correct, next step is: run `/plan` (use the repo `/specify` tooling) to
generate a development plan (contracts, data-model.md, quickstart.md). I can proceed to
execute the `/plan` step and prepare tasks if you want.
