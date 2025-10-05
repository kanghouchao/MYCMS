<!--
Sync Impact Report:
Version: NONE → 1.0.0
Constitution created from scratch with the following principles:
- I. GitHub Flow & Branch Management (NEW)
- II. Specification-Driven Development (NEW)
- III. Test-First Development (NEW)
- IV. Code Quality & Best Practices (NEW)
- V. Performance & Scalability (NEW)
- VI. Security-First Design (NEW)
- VII. Observability & Monitoring (NEW)
- VIII. Documentation Excellence (NEW)

Templates Requiring Updates:
✅ plan-template.md - Constitution Check section aligned
✅ spec-template.md - Quality gates aligned
✅ tasks-template.md - TDD workflow enforced
⚠ AGENTS.md - Review for alignment (pending)
⚠ .github/pull_request_template.md - Quality gates aligned (pending)
⚠ .github/copilot-instructions.md - Review for alignment (pending)

Follow-up TODOs: None

Created: 2025-10-05
-->

# Oli CMS Project Constitution

## Core Principles

### I. GitHub Flow & Branch Management

**MUST Requirements:**
- All development MUST follow GitHub Flow: create feature branch → commit changes → open PR → review → merge → deploy
- Feature branches MUST be named using the pattern `feature/<descriptive-name>` or `<issue-number>-<descriptive-name>`
- Pull requests MUST use the template defined in `.github/pull_request_template.md` — no exceptions
- Every PR MUST include: context & motivation, scope of changes, quality gates checklist, test notes, risk assessment
- PRs MUST be small and focused — split unrelated changes (frontend/backend/infra) into separate PRs
- Commits MUST follow Conventional Commits format: `type(scope): summary` (≤70 characters)
- Commit messages MUST reference related issues using `Fixes #<id>`, `Closes #<id>`, or `Refs #<id>`

**Rationale:** GitHub Flow ensures code quality through peer review, maintains clean history, and enables rapid rollback. Small, focused PRs reduce review burden and merge conflicts, accelerating delivery cycles.

### II. Specification-Driven Development

**MUST Requirements:**
- Every feature MUST start with a specification document in `docs/specs/<feature-name>/spec.md`
- Specifications MUST define WHAT users need and WHY, not HOW to implement
- Specifications MUST include: user scenarios, acceptance criteria, functional requirements, key entities (if data involved)
- All ambiguities MUST be marked with `[NEEDS CLARIFICATION: specific question]` and resolved before implementation
- Implementation plans MUST be created in `docs/specs/<feature-name>/plan.md` after spec approval
- API specifications MUST be maintained as OpenAPI files in `docs/specs/`
- Requirements MUST be testable and unambiguous

**Rationale:** Specification-driven development aligns team understanding before coding begins, reduces rework, and provides living documentation that evolves with the system. API-first design enables parallel frontend/backend development.

### III. Test-First Development (NON-NEGOTIABLE)

**MUST Requirements:**
- Test-Driven Development (TDD) is MANDATORY for all new features and bug fixes
- Workflow: Write tests → Get approval → Tests fail → Implement → Tests pass → Refactor
- Red-Green-Refactor cycle MUST be strictly enforced
- Contract tests MUST be written for all API endpoints before implementation
- Integration tests MUST cover: inter-service communication, middleware behavior, database interactions, tenant routing
- Unit test coverage MUST meet ≥70% line coverage threshold (enforced by Jacoco for backend, Jest for frontend)
- Tests MUST be colocated with implementation: `*.test.ts(x)` for frontend, `src/test/java` mirroring structure for backend
- Performance tests MUST validate critical paths meet targets (e.g., API response <200ms p95)

**Rationale:** TDD prevents defects at the source, clarifies requirements through executable specifications, enables fearless refactoring, and ensures regression safety. High coverage gates protect production quality.

### IV. Code Quality & Best Practices

**MUST Requirements:**
- All code MUST pass automated linting and formatting before commit
- Backend MUST follow Google Java Format via Spotless (`make lint service=backend`, `make format service=backend`)
- Frontend MUST follow Prettier (2-space, single quotes) and ESLint rules (`make lint service=frontend`, `make format service=frontend`)
- Java packages MUST be lowercase (`com.cms.<module>`), DTOs MUST use Request/Response suffixes
- React components/pages MUST use PascalCase, hooks MUST start with `use`, API clients MUST live in `frontend/src/services`
- Code MUST be idiomatic and follow language/framework conventions (Lombok/Spring for backend, React/Next.js for frontend)
- No commented-out code, no unused imports, no TODO comments without associated issues
- Cyclomatic complexity MUST be minimized — prefer extraction over nested conditionals

**Rationale:** Consistent style reduces cognitive load, automated formatting eliminates bikeshedding, and enforced conventions make code reviewable. High-quality code is maintainable code.

### V. Performance & Scalability

**MUST Requirements:**
- API endpoints MUST respond within <200ms at p95 under expected load
- Database queries MUST be optimized with proper indexing (verified via `EXPLAIN ANALYZE`)
- N+1 query problems MUST be eliminated (use JOIN or batch loading)
- Caching strategy MUST be implemented for read-heavy operations (Redis for session/tenant metadata)
- Pagination MUST be implemented for list endpoints (default page size: 20, max: 100)
- Resource limits MUST be configured: connection pool sizes, thread pools, memory limits
- Performance tests MUST be included for critical user paths (e.g., login, content fetch, search)
- Horizontal scaling MUST be possible (stateless services, externalized sessions, database connection pooling)

**Rationale:** Performance is a feature. Scalable architecture prevents technical debt and enables growth. Measured performance targets prevent degradation over time.

### VI. Security-First Design

**MUST Requirements:**
- All secrets MUST be externalized (never committed to repository) — use `.env` files based on `environment/docker-compose.example.yml`
- Authentication MUST use stateless JWT tokens with proper expiration and refresh mechanisms
- Authorization MUST enforce role-based access control (admin vs. tenant) at API gateway level
- Input validation MUST be applied to all user inputs (backend: Spring Validation, frontend: Zod or similar)
- SQL injection MUST be prevented via parameterized queries/ORM (Spring Data JPA)
- CORS policies MUST be explicitly configured (no wildcard origins in production)
- Security headers MUST be set: `X-Content-Type-Options`, `X-Frame-Options`, `Strict-Transport-Security`
- Dependency vulnerabilities MUST be monitored (Dependabot enabled, reviewed weekly)
- Health endpoints MUST NOT expose sensitive information (`/actuator/health` configured appropriately)

**Rationale:** Security breaches destroy trust and regulatory compliance. Defense-in-depth and secure-by-default configurations minimize attack surface.

### VII. Observability & Monitoring

**MUST Requirements:**
- All API responses MUST include `X-Request-ID` header for request tracing
- Structured logging MUST include correlation fields: `req=<id>`, `tenant=<value>`, `user=<id>` (where applicable)
- Log levels MUST be appropriate: ERROR for actionable failures, WARN for degraded states, INFO for business events, DEBUG for diagnostics
- Health probes MUST be exposed: `/actuator/health` (overall), `/actuator/health/liveness`, `/actuator/health/readiness`
- Readiness probes MUST check dependent services (database, Redis) before reporting healthy
- Metrics MUST be collected for: request rate, response time, error rate, database connection pool usage
- Distributed tracing context MUST propagate across service boundaries (backend → frontend → external APIs)

**Rationale:** Observability enables rapid incident response, root cause analysis, and proactive issue detection. Correlation IDs make multi-service debugging tractable.

### VIII. Documentation Excellence

**MUST Requirements:**
- README MUST be kept current with: quick start, tech stack, architecture diagram, useful commands
- API documentation MUST be generated from OpenAPI specs (Swagger UI or similar)
- Architecture Decision Records (ADRs) MUST be created for significant technical choices (stored in `docs/adr/`)
- Code comments MUST explain WHY, not WHAT (the code itself explains what)
- Public APIs and complex algorithms MUST include inline documentation (Javadoc for backend, JSDoc for frontend)
- Breaking changes MUST be documented with migration guides
- Runbook documentation MUST exist for operational procedures (deployment, rollback, data migrations)

**Rationale:** Documentation is code for humans. Living documentation reduces onboarding time, prevents knowledge silos, and enables confident evolution.

## Quality Gates

### Pre-Commit Gates
- Linting passes (`make lint service=frontend|backend`)
- Unit tests pass locally
- No merge conflicts with target branch

### Pre-Merge Gates (CI)
- All automated tests pass (unit + integration + contract)
- Code coverage meets threshold (≥70% lines)
- No high/critical security vulnerabilities (CodeQL + Dependabot)
- Build succeeds for all services
- Docker images build successfully

### Pre-Deployment Gates
- PR approved by at least one reviewer
- Quality gates checklist completed in PR description
- Smoke validation documented and executed
- Risk assessment completed
- Rollback plan documented (for risky changes)

## Development Workflow

### Feature Development Lifecycle
1. **Specification Phase**: Create feature spec in `docs/specs/<feature>/spec.md` → Get approval
2. **Planning Phase**: Create implementation plan in `docs/specs/<feature>/plan.md` → Design review
3. **Task Breakdown Phase**: Generate `docs/specs/<feature>/tasks.md` with TDD workflow
4. **Implementation Phase**: Execute tasks (tests first, then implementation, then refactor)
5. **Review Phase**: Open PR using template → Address feedback → Merge
6. **Deployment Phase**: Deploy via CI/CD → Monitor metrics → Validate in production

### Code Review Guidelines
- **Reviewers MUST verify**: Constitution compliance, test coverage, security considerations, performance impact
- **Reviewers SHOULD check**: Code clarity, edge case handling, error handling, backward compatibility
- **Review turnaround target**: 1 business day for small PRs, 2 days for large PRs
- **Approval criteria**: At least 1 approval required, all CI checks pass, no unresolved conversations

## Governance

**Authority and Enforcement:**
- This constitution supersedes all other development practices and guidelines
- All pull requests MUST verify compliance with constitutional principles (automated + manual review)
- Deviations MUST be explicitly justified in PR description and approved by project maintainers
- Complexity increases MUST be justified with clear rationale or rejected

**Amendment Process:**
- Amendments require: proposal in issue, rationale, migration plan for affected code/docs
- Version bump follows semantic versioning:
  - **MAJOR**: Backward-incompatible governance changes, principle removals/redefinitions
  - **MINOR**: New principles added, materially expanded guidance
  - **PATCH**: Clarifications, wording fixes, non-semantic refinements
- Amendments effective after merge to default branch and propagation to dependent templates

**Compliance Review:**
- Monthly constitution compliance audit (review recent PRs for adherence)
- Template consistency check whenever constitution is amended
- Onboarding materials MUST reference constitution for new contributors

**Runtime Guidance:**
- For AI/agent-specific development guidance, refer to:
  - GitHub Copilot: `.github/copilot-instructions.md`
  - General agents: `AGENTS.md`
  - Command-specific prompts: `.github/prompts/*.prompt.md`

**Version**: 1.0.0 | **Ratified**: 2025-10-05 | **Last Amended**: 2025-10-05