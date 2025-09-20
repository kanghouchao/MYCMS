<!--
Sync Impact Report

- Version change: unknown -> 1.0.0
- Modified principles: (template placeholders replaced with concrete, repo-aligned principles)
	- PRINCIPLE_1_NAME (template) -> Multi-Tenancy by Host
	- PRINCIPLE_2_NAME (template) -> Split Architecture & API Contract
	- PRINCIPLE_3_NAME (template) -> Test & Quality Gates (NON-NEGOTIABLE)
	- PRINCIPLE_4_NAME (template) -> Observability & Simplicity
	- PRINCIPLE_5_NAME (template) -> Versioning, Governance & Releases
- Added sections: Additional Constraints; Development Workflow
- Removed sections: none
- Templates requiring updates:
	- .specify/templates/plan-template.md ⚠ pending (file not found)
	- .specify/templates/spec-template.md ⚠ pending (file not found)
	- .specify/templates/tasks-template.md ⚠ pending (file not found)
	- .specify/templates/commands/* ⚠ pending (directory not found)
	- README.md ✅ checked — aligns with principles (no edit performed)
- Follow-up TODOs:
	- TODO(RATIFICATION_DATE): original ratification date unknown — please supply for record
	- Create or update `.specify/templates/` to reference cookie and API contracts where applicable
-->

# Oli CMS Constitution

## Core Principles

### Multi-Tenancy by Host
Every deployment MUST treat hostnames as the source of tenant role. The frontend middleware
MUST validate tenant domains via the central tenant validation API before rendering tenant
templates. Server-side components MUST read tenant metadata from the cookie contract set by
middleware (`x-mw-role`, `x-mw-tenant-template`, `x-mw-tenant-id`, `x-mw-tenant-name`). Tenant
validation responses accepted by the middleware MUST include either the legacy shape
(`{ valid, template_key, tenant_id, tenant_name }`) or the current shape
(`{ id, name, domain, email }`). Rationale: consistent, testable routing and clear separation
between central/admin and tenant contexts reduces cross-tenant leaks and simplifies SSR.

### Split Architecture & API Contract
The project MUST preserve a split frontend/backend architecture. All frontend API calls MUST
go through the reverse proxy under the `/api` prefix so Traefik can route and strip the
prefix before backend consumption. Backend namespaces MUST use `/central/*` for admin APIs
and `/tenant/*` for tenant-specific APIs. Frontend code MUST use the shared axios client
(`frontend/src/lib/client.ts`) for API requests; ad-hoc axios instances are PROHIBITED.
Rationale: a single routing contract reduces accidental coupling and makes local development
and proxying predictable.

### Test & Quality Gates (NON-NEGOTIABLE)
Automated tests are required for all new behavior and contract changes. The repository MUST
run unit, integration, and CI quality gates (including CodeQL scans and dependency updates)
before merging. PRs MUST include passing CI and justification for any skipped tests. Rationale:
ensure correctness across the split architecture and catch regressions early.

### Observability & Simplicity
All services and frontend server components MUST emit structured logs and expose health
endpoints (`/actuator/health` for backend). Start simple: prefer minimal, well-documented
solutions (YAGNI). Instrumentation MUST be sufficient to debug multi-tenant routing and auth
issues without introducing excessive complexity. Rationale: fast, reliable troubleshooting in
multi-tenant environments.

### Versioning, Governance & Releases
Constitutional and API changes MUST follow semantic versioning for the project and this
document. The constitution uses MAJOR.MINOR.PATCH where:
- MAJOR: backward-incompatible governance or principle removals/rewrites
- MINOR: addition of a principle or material expansion
- PATCH: wording clarifications, typos, or non-semantic edits
Release workflow MUST follow the repository GitHub Flow: branch from `master`, open PR,
pass CI and CodeQL, then squash-merge. Rationale: predictable change history and clear audit.

## Additional Constraints

Technology and security constraints for the project include:
- Languages & frameworks: Java 21+ (backend), Spring Boot 3.5+; Next.js 14+ (frontend);
	TypeScript 5+ where applicable.
- Runtime: PostgreSQL 16+, Redis 7+, Traefik 3.5+, Docker for containerized development.
- Secrets: never commit credentials or secrets to the repo; prefer environment variables and
	external secret stores for CI and production.
- IDs: persisted IDs MAY use custom generators (e.g., SnowflakeId); their use MUST be
	documented where applied.

## Development Workflow

- Build & run locally using Make targets: `make build` then `make up` (or the documented
	alternatives). Use `make logs service=<name>` to inspect service logs.
- Tests: use `make test` or `make test service=backend|frontend` as appropriate. PRs MUST
	include test results or a justification for any skipped tests.
- Local domain mapping: update `/etc/hosts` with `oli-cms.test` (admin) and tenant hosts for
	local end-to-end testing.
- Code review: open PRs with a clear description, quality gates, and the linked issue when
	applicable. Follow the repository PR template and squash-merge when approved.

## Governance

Amendments to this constitution MUST be proposed via a pull request against `master`. Each
amendment PR MUST include:
- A clear changelog entry describing the change and its rationale.
- A version bump according to the rules above and the targeted new `CONSTITUTION_VERSION`.
- A migration or compliance plan for any breaking governance change.

All PRs that amend the constitution MUST pass CI checks and receive at least one approver.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original ratification date unknown | **Last Amended**: 2025-09-20
