# Phase 0 — Research: Dependabot PR rebase & merge workflow

Path: `/Users/kanghouchao/CodeProjects/cms/specs/001-dependabot-updates/research.md`

## Unknowns / NEEDS CLARIFICATION
- FR-006: Does the team want a fully automated flow (bot) or an operator-driven manual workflow? Decision recorded below.

## Decision: Operator-first
- Decision: Start with a documented, operator-run process. Defer full automation until we have tests, CI safety checks, and a rollback strategy.

Rationale:
- Manual workflow is lower risk and allows maintainers to inspect and resolve rebase conflicts case-by-case.
- The repo already has CI and tests; implementing reliable automation requires test coverage and clear error handling (conflict resolution, CI failures) before enabling bots with force-push rights.

Alternatives considered:
- Fully automated bot: Pros — scales, low manual effort; Cons — risk of force-pushing bad rebases and merging failing PRs unless CI/approval gating is enforced.

Follow-ups:
1. Add a short automation feasibility spike: determine required test coverage and CI policies to safely allow automation (Task A in tasks.md).
2. Prepare minimal automation scaffold (scripts + tests) only after successful spike.
