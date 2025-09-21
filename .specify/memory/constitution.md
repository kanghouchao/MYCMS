# MYCMS Constitution
<!--
Sync Impact Report
Version change: 2.1.1 -> 2.1.2
Modified principles:
 - I. Code Quality & Maintainability (new wording / explicit rules)
 - II. Test-First & Verifiable Quality (clarified non-negotiable testing rules)
 - III. User Experience Consistency (newly explicit UX/accessibility requirements)
 - IV. Performance & Scalability Requirements (expanded to include budgets/tests)
 - V. Observability, Versioning & Simplicity (consolidated observability and versioning)
Added sections:
 - Non-Functional Requirements & Constraints
 - Development Workflow & Quality Gates
Removed sections:
 - None
Templates updated:
 - .specify/templates/plan-template.md (version reference updated) ✅
 - .specify/templates/spec-template.md (no changes required) ✅
 - .specify/templates/tasks-template.md (no changes required) ✅
Commands/templates missing or reviewed:
 - .specify/templates/commands/ : directory not present ⚠ pending (verify command templates referencing constitution)
Follow-up TODOs:
 - TODO(RATIFICATION_DATE): determine original ratification date and replace placeholder.
 - Run a quick review for any external docs referencing Constitution v2.1.1 (CHANGELOG, README).
-->

## Core Principles

### I. Code Quality & Maintainability
All code MUST be easy to read, deliberately structured, and reviewed. Specific requirements:

- Source files MUST follow the project's style guides (formatters, linters) and pass automated checks before merge.
- Public interfaces (APIs, library surfaces, module exports) MUST include concise documentation and an example of intended use.
- Complexity increases MUST be justified in the PR description and accompanied by tests and a migration plan when applicable.
- Technical debt MAY be tracked in explicitly tagged issues but MUST include a remediation timeline.

Rationale: High-quality, consistent code reduces review time, lowers the chance of regressions, and makes long-term maintenance feasible.

### II. Test-First & Verifiable Quality
Testing is non-negotiable. The project follows a test-first, TDD-friendly approach where practical:

- All new features MUST include failing tests (unit/contract/integration) that demonstrate expected behavior before implementation.
- Test coverage targets: maintain a minimum baseline of 70% project-wide; critical modules (auth, billing, core domain) SHOULD target 90%.
- Continuous Integration gates MUST run unit, integration, and static-analysis checks; merges are blocked on failing gates.
- Performance and load tests MUST be added for features that affect latency, throughput, or resource usage.

Rationale: Tests provide safety for refactors, serve as executable documentation, and are the primary way we assert compliance with other principles.

### III. User Experience Consistency
User-facing behavior MUST be predictable, accessible, and coherent across the product surface:

- UI and API contracts MUST provide consistent affordances (error formats, pagination, validation messages).
- Visual and interaction patterns in the frontend MUST follow the shared component library and global styles; exceptions MUST be justified.
- Accessibility best practices (semantic HTML, keyboard navigation, ARIA where required) MUST be considered in all UI work.
- UX changes that alter user flow or expectations MUST be accompanied by a brief user impact note in the PR.

Rationale: Consistency reduces user friction, lowers support costs, and improves trust in the product.

### IV. Performance & Scalability Requirements
Non-functional performance requirements are part of the product contract:

- Service endpoints designated as "performance-critical" MUST document targets (e.g., p95 latency < 200ms under defined load) and include benchmark tests.
- Memory and resource budgets SHOULD be specified for long-running services and enforced in CI where feasible.
- Load and soak tests MUST be run for changes affecting cache, DB indexes, or high-throughput paths.
- Design choices that trade latency for consistency or durability MUST be explicitly documented with rationale and fallback behavior.

Rationale: Explicit performance targets make trade-offs visible and prevent regressions that impact customers.

### V. Observability, Versioning & Simplicity
Systems MUST be observable and follow clear versioning and simplicity rules:

- Structured logging and contextual request identifiers MUST be present for server-side flows to enable tracing and debugging.
- Metrics (request rates, error rates, latencies) MUST be emitted for services and scraped by monitoring systems.
- Semantic versioning (MAJOR.MINOR.PATCH) MUST be used for public packages and APIs; breaking changes MUST be MAJOR and documented with migration notes.
- Prefer simple, well-understood solutions (YAGNI) unless complexity is justified and documented.

Rationale: Observability shortens time-to-resolution; versioning clarifies compatibility; simplicity reduces cognitive load.

## Non-Functional Requirements & Constraints

Technology stack (in-repo guidance):

- Backend: Java 21 (Spring Boot preferred where present in this repo).  
- Frontend: Next.js with TypeScript.  

Security and compliance:

- Security-sensitive changes MUST include threat modeling notes and follow secure defaults (least privilege, sanitized inputs, secret handling).  
- Secrets MUST not be committed; use approved secrets managers for deployments.

Performance and resource constraints:

- Define and document p95/p99 latency targets for public endpoints where applicable.  
- Aim for reasonable resource footprints on typical hosting (e.g., memory budgets documented per service).

Deployment:

- Production deployments MUST be automated and repeatable; rollback plans SHOULD be provided for high-risk changes.

Rationale: Centralizing non-functional constraints avoids ad-hoc decisions that can cause outages or compliance violations.

## Development Workflow & Quality Gates

Code review and branches:

- Feature branches MUST follow the Spec Kit naming convention: branch names MUST start with a three-digit numeric prefix followed by a hyphen (regexp: `^[0-9]{3}-`).
- Every PR MUST include a short description of the change, testing steps, and links to relevant spec/plan artifacts.
- At least one approving review from a repository maintainer or component owner is REQUIRED for merges; larger or riskier changes MAY require two approvers.

CI and gates:

- CI MUST run linters, formatters, unit tests, integration tests, and security scanners for all branches.  
- Merges to protected branches are blocked until all required checks pass.

Quality thresholds:

- Test baselines and static-analysis tolerances SHOULD be set in CI config; deviations require explicit approval and a mitigation plan.

Release process:

- Releasing public or shared libraries MUST follow semantic versioning and include changelogs and migration guidance when appropriate.

Rationale: A predictable workflow reduces merge conflicts, enforces quality, and clarifies responsibilities.

## Governance
<!-- Constitution supersedes informal practices; follow amendment procedure below -->

This Constitution is the canonical set of guiding principles for the MYCMS project. It supersedes informal practices when a clear conflict exists. Key governance rules:

- Amendments to this Constitution MUST be proposed as a documented change (PR) with:
	- A clear description of the proposed change and the problem it solves.
	- Impact analysis (what templates, plans, or automation will need updates).
	- A migration or compliance plan for existing work if the change is breaking.
- Constitutional amendments that add or materially change principles (new obligations) MUST be reviewed and approved by at least two maintainers or by the project governance group.
- Minor clarifications (typo fixes, wording clarifications) MAY be applied via a single maintainer PR but MUST be documented in the change log.
- Compliance checks: New /plan outputs and generated tasks MUST include a "Constitution Check" section; CI/merge gates SHOULD validate obvious violations where feasible.

**Version**: 2.1.2 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date unknown | **Last Amended**: 2025-09-21
