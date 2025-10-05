# Pull Request Template

**Constitution Compliance**: This PR MUST comply with the Project Constitution (`.specify/memory/constitution.md` v1.0.0). All quality gates below are derived from constitutional principles.

## PR Title

Short, imperative summary (e.g., "Add employee management API endpoints") — follows Conventional Commits format.

## Context & Motivation

- What problem does this solve? Why now?
- Link issues: Closes #[issue-number] (and related) — **REQUIRED** per Constitution Principle I
- Feature spec reference: `docs/specs/<feature>/spec.md` (if applicable) — per Constitution Principle II
- ADR link(s) if applicable (for significant technical decisions) — per Constitution Principle VIII

## Scope of Changes

- What changed at a high level
- Public API changes (call out breaking changes explicitly)
- Affected components/services: frontend | backend | infra
- **Constitution Principle Alignment**: Which constitutional principles does this PR primarily address?

## Quality Gates (Required) ✅

**Pre-Commit Gates** (Constitution: Quality Gates section):
- [ ] Build passes locally (`make build service=frontend|backend`)
- [ ] Lint/format passes (`make lint service=frontend|backend`, `make format service=frontend|backend`)
- [ ] No merge conflicts with target branch
- [ ] Secrets externalized (no hardcoded credentials, API keys, or passwords)

**Test-First Development** (Constitution Principle III - NON-NEGOTIABLE):
- [ ] **TDD workflow followed**: Tests written → Approved → Tests failed → Implementation → Tests passed → Refactored
- [ ] Contract tests exist for all new API endpoints (if applicable)
- [ ] Integration tests cover critical paths (tenant routing, middleware, database interactions)
- [ ] Unit tests pass locally (`make test service=frontend|backend`)
- [ ] Coverage meets threshold: ≥70% lines (attach summary below)

**Performance & Scalability** (Constitution Principle V):
- [ ] API endpoints tested: response time <200ms p95 (if applicable)
- [ ] Database queries optimized: no N+1 queries, proper indexing verified
- [ ] Pagination implemented for list endpoints (default: 20, max: 100) (if applicable)
- [ ] Caching strategy applied for read-heavy operations (if applicable)

**Security-First Design** (Constitution Principle VI):
- [ ] Input validation applied to all user inputs
- [ ] Authentication/authorization verified (JWT, RBAC)
- [ ] SQL injection prevented (parameterized queries/ORM)
- [ ] CORS policies properly configured (no wildcard origins in production)
- [ ] Security headers set: `X-Content-Type-Options`, `X-Frame-Options`, `Strict-Transport-Security`

**Observability & Monitoring** (Constitution Principle VII):
- [ ] Request correlation ID propagated (`X-Request-ID` header)
- [ ] Structured logging includes: `req=<id>`, `tenant=<value>`, `user=<id>` (where applicable)
- [ ] Health probes updated if new dependencies added (`/actuator/health/readiness`)

**Code Quality & Best Practices** (Constitution Principle IV):
- [ ] Naming conventions followed (Java: lowercase packages, DTO suffixes; React: PascalCase components, `use*` hooks)
- [ ] No commented-out code, no unused imports
- [ ] TODO comments linked to issues (or removed)
- [ ] Code is idiomatic and follows framework conventions

**Documentation Excellence** (Constitution Principle VIII):
- [ ] README updated if architecture/commands changed
- [ ] API documentation generated from OpenAPI specs (if API changes)
- [ ] ADR created if significant technical decision made (`docs/adr/`)
- [ ] Code comments explain WHY, not WHAT

### Test Coverage Summary

**Backend** (if applicable):
- Line coverage: __% (threshold: ≥70%)
- Branch coverage: __%
- Jacoco report: `reports/backend/jacoco/index.html`

**Frontend** (if applicable):
- Line coverage: __% (threshold: ≥70%)
- Branch coverage: __%
- Jest report: `reports/frontend/lcov-report/index.html`

### Smoke Validation (Minimal Repro Steps)

**REQUIRED**: Document minimal steps to validate this change works as expected.

1. [Step 1: e.g., Start services with `make up`]
2. [Step 2: e.g., Login as admin at `http://my-cms.test`]
3. [Step 3: e.g., Navigate to Employees page]
4. [Step 4: e.g., Create new employee with required fields]
5. Expected: [e.g., Employee appears in list with status 200 response <200ms]

**Actual Result**: [Pass / Fail / Partial — describe]

## Test Notes

- Key test cases added/updated: [List significant test files and what they cover]
- Edge cases tested: [e.g., validation errors, empty states, permission boundaries]
- Performance test results: [e.g., p50: 85ms, p95: 145ms, p99: 180ms]

## Risk & Mitigation

- **Risk Level**: [Low / Medium / High]
- **Risk Description**: [e.g., Database schema change affects existing tenants]
- **Mitigation Strategy**: [e.g., Migration script tested on staging, rollback plan documented]
- **Rollback Plan**: [e.g., Revert migration with down.sql, restart services]

## Security Impact

- **Security Review Required?** [Yes / No]
- **Changes to Authentication/Authorization?** [Yes / No — describe]
- **New Dependencies Added?** [Yes / No — list with versions]
- **Vulnerability Scan Results**: [CodeQL: Pass / Fail, Dependabot: No alerts / X alerts]

## Impacted Files (Key)

List the critical files changed and justify changes briefly:
- `backend/src/main/java/com/cms/...`: [Purpose]
- `frontend/src/components/...`: [Purpose]
- `docs/specs/<feature>/...`: [Purpose]

## Screenshots / Logs (Optional)

Attach relevant output or images for reviewers:
- UI changes: [Screenshot]
- Log output: [Log snippet showing req=<id>, tenant=<value>]
- Performance metrics: [Grafana/metrics screenshot if available]

## Constitutional Compliance Checklist ⚖️

**GitHub Flow & Branch Management** (Principle I):
- [ ] Feature branch follows naming convention: `feature/<name>` or `<issue>-<name>`
- [ ] Changes focused and scoped for single PR (no unrelated formatting/renames)
- [ ] Commit messages follow Conventional Commits: `type(scope): summary` (≤70 chars)

**Specification-Driven Development** (Principle II):
- [ ] Feature spec exists and approved (if new feature): `docs/specs/<feature>/spec.md`
- [ ] All `[NEEDS CLARIFICATION]` markers resolved before implementation
- [ ] API contracts defined as OpenAPI specs (if API changes)

---

## AI Agent Notes

**For AI-generated PRs** (GitHub Copilot, Claude, Gemini, etc.):
- Keep changes minimal and focused; avoid unrelated formatting/renames
- After multi-file edits, include a brief progress summary (what changed, why, next)
- Follow repo-specific constitution in `.specify/memory/constitution.md`
- Verify all constitutional principles addressed in checklist above
- If any quality gate cannot pass, document justification in "Risk & Mitigation" section

---

**Template Version**: Aligned with Constitution v1.0.0 | **Last Updated**: 2025-10-05
