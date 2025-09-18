# Pull Request Template

## PR Title

Short, imperative summary (e.g., "Add PR template and AI submission guidelines").

## Context & Motivation

- What problem does this solve? Why now?
- Link issues: Closes #[issue-number] (and related)
- ADR link(s) if applicable

## Scope of Changes

- What changed at a high level
- Public API changes (call out breaking changes explicitly)
- Affected components/services: frontend | backend | infra

## Quality Gates (Required)

- [ ] Build passes locally
- [ ] Lint/Typecheck passes
- [ ] Unit tests pass
- [ ] Coverage meets threshold (target ≥ 70% lines; attach summary)
- [ ] Smoke validation (minimal reproducible steps below)

### Smoke Validation (Minimal Repro Steps)

1. ...
2. ...
3. Expected: ...

## Test Notes

- Key test cases added/updated
- Coverage summary (numbers + link to report artifact if available)

## Risk & Mitigation

- Risk: ...
- Mitigation/rollback plan: ...

## Security Impact

- Dependencies changed? Vulnerability notes? (N/A if none)

## Impacted Files (Key)

- List the critical files and justify changes briefly

## Screenshots / Logs (Optional)

Attach relevant output or images for reviewers.

## AI Agent Notes

- Keep changes minimal and focused; avoid unrelated formatting/renames
- After multi-file edits, include a brief progress summary (what changed, why, next)
- Follow repo-specific Copilot instructions in `.github/copilot-instructions.md`
