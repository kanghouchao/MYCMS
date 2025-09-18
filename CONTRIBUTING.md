# Contributing Guide

This project welcomes contributions from humans and AI coding agents. Please follow these rules to keep changes safe, reviewable, and fast to ship.

## Principles

- Minimal, focused changes: modify only what is necessary for the task
- Keep public APIs stable; call out any breaking change explicitly
- Prefer small, incremental PRs; keep `master` green and deployable
- Always provide a minimal repro (smoke) and test evidence

## Quality Gates

Before requesting review, ensure the following succeed locally:

1. Build passes
2. Lint/Typecheck passes
3. Unit tests pass
4. Coverage meets threshold (initial target ≥ 70% lines)
5. Smoke validation steps are documented in the PR

## AI Submission Guidelines

- Read `.github/copilot-instructions.md` and repository-specific guidance
- After multi-file edits, add a short progress summary in the PR (what changed, why, next)
- Avoid reformatting unrelated files; keep diffs minimal
- Do not add new HTTP clients; use shared clients (`frontend/src/lib/client.ts`)
- Respect architecture conventions (controller namespaces, API prefixes, cookies contract)

## Workflow

- Use GitHub Flow: issue → branch → PR → review → squash & merge
- One PR per issue; link the issue in the PR (e.g., `Closes #123`)
- Name branches like `<area>/issue-<no>-<slug>` (e.g., `chore/issue-13-ai-pr-template`)
- PR descriptions must follow `.github/pull_request_template.md`. CI will check for required sections and fail otherwise.

## Tests & Coverage

- Frontend: Jest, Testing Library; put tests under `frontend/src/**/__tests__/**`
- Backend: JUnit; generate Jacoco report to `backend/build/reports/jacoco`
- Coverage targets evolve; see ADRs in `docs/adr/`

## Security

- Prefer dependency updates through Dependabot PRs
- For security-sensitive changes, describe the impact in the PR template section

Thanks for contributing!
