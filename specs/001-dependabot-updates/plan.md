Implementation Plan: Dependabot PR rebase & merge workflow

Branch: `001-dependabot-updates` | Date: 2025-09-21 | Spec: `/Users/kanghouchao/CodeProjects/cms/specs/001-dependabot-updates/spec.md`
Input: feature specification at `/Users/kanghouchao/CodeProjects/cms/specs/001-dependabot-updates/spec.md`

## Execution Flow (plan scope)
1. Load feature spec from Input path
   → If not found: ERROR "No feature spec at {path}"
2. Fill Technical Context (scan for NEEDS CLARIFICATION)
   → Detect project type from repo: backend (Java 21) + frontend (Next.js + TypeScript)
3. Constitution Check: ensure design follows constitution v2.1.3 principles (tests, CI, branch naming, non-breaking changes)
4. Phase 0: research.md (resolve ambiguities, especially automation vs manual)
5. Phase 1: produce data-model.md, contracts/, quickstart.md (operator-run steps + CI verification)
6. Phase 2: tasks.md — concrete, ordered tasks for implementation and verification
7. Post-design Constitution re-check and complexity tracking

## Summary
Primary requirement: provide a safe, auditable process for maintainers to rebase Dependabot-created PR branches onto `main`, force-push the rebased branch, and merge the PRs. Ensure CI runs and passes before merging and close the coordinating branch `001-dependabot-updates` when complete.

Rationale: Dependabot PRs often contain only dependency bumps; rebasing onto current `main` and validating CI reduces merge conflicts and keeps history tidy. This plan emphasizes an initial operator-driven workflow with a clear decision point to automate later (see research.md).

## Technical Context
- Language/Version: Backend Java 21 (present in repo), Frontend Next.js + TypeScript (present in repo)
- Primary Dependencies: Gradle (backend), Node/npm (frontend), GitHub for PRs and CI
- Storage: N/A
- Testing: Existing backend/unit tests (Gradle), frontend tests (Jest). CI gates run on PRs.
- Target Platform: Linux/macOS developer machines; CI runs in GitHub Actions
- Project Type: Web app (frontend + backend)
- Constraints: All changes must respect the Constitution (tests-first where reasonable), branch naming, and PR template requirements.

## Constitution Check
Gates applied from `/Users/kanghouchao/CodeProjects/cms/.specify/memory/constitution.md`:
- Code quality & maintainability: new artifacts must include failing tests for new automation code.
- Test-first: any automation must be covered by tests; manual workflow must provide reproducible verification steps.
- Workflow: branch naming and PR templates must be followed.

No violations identified for an operator-first approach. If automation is added later, ensure test coverage and CI gating before merge.

## Project Structure (docs)
```
specs/001-dependabot-updates/
├─ spec.md           # original feature spec
├─ plan.md           # this file
├─ research.md       # Phase 0 output
├─ data-model.md     # Phase 1 output
├─ quickstart.md     # Phase 1 output (operator steps)
├─ contracts/
│  ├─ README.md
│  └─ workflow.yaml
└─ tasks.md          # Phase 2 output (task list)
```

## Phase 0: Outline & Research (research.md)
- Resolve ambiguities (notably FR-006: automation vs manual).
- Decide immediate scope: operator-driven, documented, repeatable flow; automation deferred until tests and safety checks exist.

## Phase 1: Design & Contracts
1. Data model: identify key entities (Dependabot PR, Coordinator Branch, CI Status).
2. Contracts: human/operational contract describing steps and expected results (in `contracts/workflow.yaml`).
3. Quickstart: operator step-by-step guide (`quickstart.md`) including preconditions and verification commands for macOS (zsh).

## Phase 2: Task Planning (tasks.md)
- Generate an ordered task list (see `tasks.md`). Tasks include research follow-ups, create automation scaffolding (if decided), and verification tests.

## Next Steps
1. Follow tasks in `tasks.md` to implement automation (optional) or to execute the operator flow and close coordinator branch.
2. If automation is implemented: add failing tests and CI checks, then re-run Constitution Check before merging.
