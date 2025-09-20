# <!--
# Sync Impact Report
# Version change: 1.0.0 -> 1.1.0
# Bump rationale: MINOR — added Code Quality, Testing and Commit/PR governance sections
# Modified principles:
# - Added: Code Quality & Testing
# - Added: Commit and PR Standards (GitHub Flow enforcement)
# - Clarified: cross-reference to `.github/copilot-instructions.md` for agent execution patterns
# Added sections:
# - Code Quality & Testing
# - Commit and PR Standards
# - Contributor Workflow: GitHub Flow enforcement
# Templates reviewed and status:
# - .specify/templates/plan-template.md: ✅ updated (version note set to v1.1.0)
# - .specify/templates/spec-template.md: ⚠ pending (placeholder markers unchanged; no forced edits)
# - .specify/templates/tasks-template.md: ⚠ pending (placeholder markers unchanged)
# Follow-up TODOs:
# - TODO(RATIFICATION_DATE) remains; maintainers must fill the original adoption date.
# - Review `.specify/templates/agent-file-template.md` to remove agent-specific tokens if needed.
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

### Code Quality & Testing

- All production code MUST include automated tests. New features MUST include unit,
  integration, and (where applicable) contract tests. Tests MUST be written before
  implementation (TDD): tests are committed and pushed to feature branches first.
- Tests MUST be readable, deterministic, and fast for CI (aim for <60s unit test
  suite where practical). Time-consuming integration tests should be marked and run
  in CI stages that tolerate longer runtimes.
- Code style and linting MUST be enforced by CI. Use the repository-approved tools
  (frontend: ESLint/TypeScript rules in `package.json`; backend: Spotless/Checkstyle
  or Gradle plugins). Failing linters block merges.
- Documentation: public modules, exported functions, and APIs MUST have concise
  inline docs and a short usage example in the repository docs. Maintain a `docs/`
  directory for design notes and migration guides.

### Commit and PR Standards (GitHub Flow)

- Branching: create short-lived branches from `master` named `issue-<id>-short-desc`
  or `feat/<short-desc>`; avoid long-lived feature branches.
- Commits: use conventional commits style (e.g., `feat:`, `fix:`, `docs:`, `chore:`,
  `test:`, `refactor:`). Commit messages MUST be imperative and reference issue IDs
  when applicable (e.g., `feat: add tenant validation (Closes #123)`).
- PRs: open PRs against `master`. Each PR MUST use the repository PR template and
  include: scope, risk, testing steps, Quality Gates status, and changelog notes.
- Reviews: at least one approving review is required for non-trivial changes. The
  maintainer(s) may request additional reviewers for infra, security, or data
  model changes.
- Merge strategy: squash and merge only. Delete branch after merge.

### Documentation & Release Notes

- Every change that affects behavior visible to integrators or tenants MUST include
  a short changelog entry. Changelogs are generated from PR descriptions and
  consolidated in `CHANGELOG.md` following semantic versioning.


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

Note: Operational agent guidance and execution patterns (terminal safety, Makefile
usage, MCP guidance) are maintained in `.github/copilot-instructions.md` and the
`.specify/templates/*` files. The Constitution is authoritative for governance and
principles; implementation and agent-run patterns should reference the copilot
instructions to avoid duplication.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): provide original adoption date | **Last Amended**: 2025-09-20
