# <!--
# Sync Impact Report
# Version change: template/example v2.1.1 (example) -> 1.0.0
# Modified principles:
# - Added: Tenant-first Multi-tenant Design (explicit for this repo)
# - Added: Split Architecture & Clear API Boundaries
# - Clarified: Test-First (made NON-NEGOTIABLE and repository-specific)
# - Added: Observability & Structured Logging
# - Clarified: Semantic Versioning & Breaking Changes
# Added sections:
# - Constraints & Security Requirements
# - Development Workflow & Quality Gates
# Removed sections: none (template placeholders converted to concrete text)
# Templates reviewed and status:
# - .specify/templates/plan-template.md: ✅ updated (version note set to v1.0.0)
# - .specify/templates/spec-template.md: ⚠ pending (contains placeholder markers intended for feature specs; no change required)
# - .specify/templates/tasks-template.md: ⚠ pending (template placeholders remain; no change required)
# Follow-up TODOs:
# - TODO(RATIFICATION_DATE) inserted for Ratified date; maintainers must fill the original adoption date.
# - Review `.specify/templates/agent-file-template.md` for any agent-specific mentions to generalize if needed.
# -->

# Oli CMS Constitution

## Core Principles

### Tenant-first Multi-tenant Design
Every change MUST preserve tenant isolation by default. The frontend and backend are split
architectures; requests are routed by host and tenancy is validated server-side. Templates,
data, and auth context MUST be scoped per-tenant. Rationale: multi-tenancy is the primary
value proposition of Oli CMS—tenant isolation prevents data leakage and simplifies
compliance.

### Split Architecture & Clear API Boundaries
Services MUST keep a clear separation between the Next.js frontend and the Spring Boot
backend. All frontend API calls MUST go through the reverse proxy under `/api/*` and the
backend MUST namespace administrative vs tenant APIs under `/central/*` and `/tenant/*`.
Rationale: Explicit boundaries make routing, security, and ownership clear and reduce
accidental coupling.

### Test-First (NON-NEGOTIABLE)
Every new feature MUST begin with failing tests: contract tests, integration tests, and
unit tests where appropriate. Tests MUST be checked in before implementation. The
development cycle follows Red → Green → Refactor. Rationale: TDD preserves correctness,
reduces regressions, and documents expected behavior.

### Observability & Structured Logging
All services and critical workflows MUST emit structured logs and meaningful health
metrics. Errors MUST include context (tenant id, request id) without leaking sensitive data.
Rationale: Observability is required for reliable operation and fast incident response.

### Semantic Versioning & Breaking Changes
Public APIs, templates, and shared contracts MUST follow semantic versioning. Breaking
changes MUST be communicated, documented in a migration guide, and require a minor or
major version bump depending on compatibility impact. Rationale: predictable versioning
reduces upgrade friction for tenants and integrators.

## Constraints & Security Requirements

- Secrets and credentials MUST NOT be committed to source. Use environment-managed
  secrets for deployment and `Makefile`/Compose for local development.
- Network boundaries: Traefik MUST be used as the reverse proxy; frontend calls to backend
  MUST use `/api` so Traefik can route and strip prefixes reliably.
- Cookies set by middleware (`x-mw-role`, `x-mw-tenant-template`, `x-mw-tenant-id`,
  `x-mw-tenant-name`) MUST be treated as authoritative server-side context and validated
  by backend services.
- Authentication is stateless JWT by default; any deviation (e.g., session-based auth)
  MUST be explicitly justified and reviewed.

## Development Workflow & Quality Gates

- Build and run using the repository Makefile: prefer `make build` and `make up` for local
  development. Long-running processes MUST be started in background (use `make up`).
- Use the shared axios client `frontend/src/lib/client.ts` for API calls; do not create
  ad-hoc axios instances.
- Code review: all changes require a PR against `master`; include Quality Gates: build,
  lint/typecheck, unit tests, and smoke tests. PRs MUST reference the related issue.
- Testing policy: run `make test` before merge; failing tests block merges.

## Governance

Amendments to this Constitution require a documented PR describing the change, a
compatibility assessment, and approval from the repository maintainer(s). Versioning of
the constitution follows semantic versioning rules:

- MAJOR: Backwards-incompatible governance or principle removals/redefinitions.
- MINOR: Addition of a new principle/section or material expansion of guidance.
- PATCH: Clarifications, wording fixes, or non-semantic refinements.

Change process:

1. Propose amendment in a PR that includes: rationale, migration steps, and tests or
   checks (if applicable).
2. CI MUST pass (build, lint, tests). 3rd-party security checks (CodeQL) SHOULD run
   before merge when security-relevant changes are present.
3. Maintain compatibility notes and update dependent templates (`.specify/templates/*`) and
   automation scripts as part of the PR.

Compliance expectations:

- Tooling (plans, specs, tasks generators) MUST automatically include a "Constitution
  Check" step and reject or flag work that violates non-negotiable principles (e.g., test-
  first violations, tenant isolation failures).

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): provide original adoption date | **Last Amended**: 2025-09-20
