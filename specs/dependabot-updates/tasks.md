```markdown
# Tasks: Dependabot updates remediation

**Feature dir**: `specs/dependabot-updates`
**Input**: `specs/dependabot-updates/plan.md`
**Goal**: Triage, verify, and safely merge Dependabot PRs #34..#25 with clear, executable commands.

## Execution flow (short)
1. Run triage tasks (tests/local smoke) per PR.
2. If triage OK, run CI validation (or re-run CI) for the PR branch.
3. Merge low-risk PRs (squash) after CI green.
4. Merge backend and workflow updates last and monitor.

## Format notes
- **[P]** means tasks can run in parallel (different files/no shared resources).
- Use exact git/cli commands below; replace <PR_BRANCH> or <PR_NUMBER> where needed.

---

## Phase 1 — Setup & Preconditions
- T001 Ensure GitHub CLI (`gh`) is available and authenticated for merge/PR operations.
  - Path: repository root
  - Command:
    ```bash
    gh auth status || gh auth login
    gh --version
    ```

---

## Phase 2 — Triage (Tests first, TDD-style)
CRITICAL: These tasks must complete before merging. Frontend tasks are independent of backend/tasks and can run [P].

- T002 [P] Triage PR #33 (axios) — smoke & unit tests
  - Affected: `frontend/package.json`
  - Commands:
    ```bash
    git fetch origin dependabot/npm_and_yarn/frontend/axios-tw-1.12.2:tmp/pr-33
    git checkout tmp/pr-33
    (cd frontend && npm ci)
    (cd frontend && npm run build)
    (cd frontend && npm test -- -i)
    (cd frontend && npm run lint)
    ```
  - Outcome: comment `triage/ok` or `triage/requires-manual-review` on PR #33.

- T003 [P] Triage PR #34 (tailwind 3.x→4.x) — smoke + migration check
  - Affected: `frontend/package.json`
  - Commands:
    ```bash
    git fetch origin dependabot/npm_and_yarn/frontend/tailwindcss-4.1.13:tmp/pr-34
    git checkout tmp/pr-34
    (cd frontend && npm ci)
    (cd frontend && npm run build)
    # Run Tailwind migration linting where applicable (project-specific)
    (cd frontend && npm test -- -i)
    ```
  - Note: Review Tailwind 4 migration notes; if incompatible, mark `triage/requires-manual-review`.

- T004 [P] Triage PR #32 (ts-jest) — tests run
  - Affected: `frontend/package.json`
  - Commands:
    ```bash
    git fetch origin dependabot/npm_and_yarn/frontend/ts-jest-tw-29.4.4:tmp/pr-32
    git checkout tmp/pr-32
    (cd frontend && npm ci)
    (cd frontend && npm test -- -i)
    ```

- T005 [P] Triage PR #31 (jest) — tests run
  - Affected: `frontend/package.json`
  - Commands:
    ```bash
    git fetch origin dependabot/npm_and_yarn/frontend/jest-30.1.3:tmp/pr-31
    git checkout tmp/pr-31
    (cd frontend && npm ci)
    (cd frontend && npm test -- -i)
    ```

- T006 [P] Triage PR #30 (@testing-library/jest-dom) — tests run
  - Affected: `frontend/package.json`
  - Commands:
    ```bash
    git fetch origin dependabot/npm_and_yarn/frontend/testing-library/jest-dom-tw-6.8.0:tmp/pr-30
    git checkout tmp/pr-30
    (cd frontend && npm ci)
    (cd frontend && npm test -- -i)
    ```

- T007 [P] Triage PR #29 (spotless 6→7) — Gradle plugin compatibility and build
  - Affected: `backend/build.gradle`
  - Commands:
    ```bash
    git fetch origin dependabot/gradle/backend/com.diffplug.spotless-7.2.1:tmp/pr-29
    git checkout tmp/pr-29
    (cd backend && ./gradlew clean build --no-daemon)
    (cd backend && ./gradlew spotlessCheck)
    ```
  - Outcome: verify plugin compatibility; if spotlessApply changes are required run `./gradlew spotlessApply` and re-run `spotlessCheck`.

- T008 [P] Triage PRs #28,#27,#26,#25 (GitHub Actions updates)
  - Affected: `.github/workflows/*`
  - For each PR (replace <pr-branch> and <pr-number>):
    ```bash
    git fetch origin <PR_BRANCH>:tmp/pr-<number>
    git checkout tmp/pr-<number>
    # Create a test branch to run CI (will execute workflows using updated actions)
    git checkout -b tmp/ci-test-<number>
    git push -u origin tmp/ci-test-<number>
    # On GitHub: re-run CI for tmp/ci-test-<number> or open a temporary PR to master to validate
    ```
  - Note: Validate runner compatibility and hosted runner versions.

---

## Phase 3 — CI Validation (after triage passes)
- T009 [P] Re-run/trigger CI for PR #33 and other frontend PRs if needed
  - Commands (example using GitHub CLI):
    ```bash
    gh pr checkout 33 || git fetch origin dependabot/npm_and_yarn/frontend/axios-tw-1.12.2:tmp/pr-33 && git checkout tmp/pr-33
    gh pr status --show mergeable
    # Trigger workflow dispatch or re-run checks via GitHub UI/CLI if available
    gh api repos/:owner/:repo/actions/workflows --silent || true
    ```
  - Outcome: Confirm CI passes (build, tests, lint, CodeQL).

---

## Phase 4 — Merge (controlled, prefer squash)
Only merge after triage + CI green. Use `gh` or comment `@dependabot squash and merge` if Dependabot is configured.

- T010 [P] Merge PR #33 (axios) — if triage/CI OK
  - Command (preferred via GitHub CLI):
    ```bash
    gh pr merge 33 --squash --delete-branch --admin
    # or comment for dependabot: gh pr comment 33 --body "@dependabot squash and merge"
    ```

- T011 [P] Merge PR #32 (ts-jest), PR #31 (jest), PR #30 (jest-dom), PR #34 (tailwind) — merge individually once CI green
  - Commands (example):
    ```bash
    gh pr merge 32 --squash --delete-branch
    gh pr merge 31 --squash --delete-branch
    gh pr merge 30 --squash --delete-branch
    gh pr merge 34 --squash --delete-branch
    ```
  - Note: For tailwind (#34) confirm migration steps first. Consider merging tailwind into a staging branch and running integration smoke tests.

- T012 Merge PR #29 (spotless) — after successful Gradle checks
  - Commands:
    ```bash
    gh pr checkout 29 || (git fetch origin dependabot/gradle/backend/com.diffplug.spotless-7.2.1:tmp/pr-29 && git checkout tmp/pr-29)
    (cd backend && ./gradlew clean build)
    gh pr merge 29 --squash --delete-branch
    ```

- T013 Merge workflow PRs (#25-#28) — one at a time, monitor CI
  - Commands (example for PR 28):
    ```bash
    gh pr merge 28 --squash --delete-branch
    # Wait for CI runs on master to complete and monitor
    ```

---

## Phase 5 — Post-merge monitoring & rollback
- T020 Monitor `master` CI and CodeQL for 60 minutes after each merge.
  - Commands:
    ```bash
    # Check recent workflow runs
    gh run list --repo $GITHUB_REPOSITORY --limit 20
    gh run watch <run-id>
    ```

- T021 Rollback plan (if regressions found)
  - Commands:
    ```bash
    # Revert merge via gh or git
    gh pr list --search "is:merged dependabot" --limit 50
    # To revert merge commit (replace <merge-sha>):
    git checkout master
    git pull origin master
    git revert <merge-sha> -m 1
    git push origin HEAD:master
    gh pr create --base master --head $(git rev-parse --abbrev-ref HEAD) --title "revert: <merge-sha>" --body "Revert due to regression"
    ```

---

## Parallel execution guidance
- Run T002–T006 (frontend triage) in parallel on separate runners or machines.
- Run T007 (backend triage) in parallel with frontend triage as it touches different files.
- Merge steps (T010–T013) should be performed one PR at a time per area to limit blast radius; merging different areas can be parallelized if CI checks are independent.

## Acceptance criteria (per plan)
- Each PR is either merged or annotated with `triage/requires-manual-review` and an issue explaining blockers.
- Master CI green after merges; CodeQL and build checks pass.
- Post-merge monitoring shows no regressions within 60 minutes.

## Helpful tips & agent commands
- Use `gh` to automate merges and comments. If `gh` is unavailable, use the GitHub API.
- When running tests locally, use `npm test -- -i` to run tests in interactive/CI mode if Jest is configured.
- Always create a temporary branch (tmp/pr-<number>) for local experiments and delete it after (or reuse the PR branch via `gh pr checkout`).

---

Generated from `specs/dependabot-updates/plan.md` — edit flow or owners as needed before executing.

```
# Dependabot remediation tasks

This file records the individual actionable tasks to triage and merge Dependabot PRs.

Generated: 2025-09-20
Branch: dependabot/remediation-tasks-001

## Overview
Tasks are grouped by affected area: frontend, backend, workflows.
Each task includes: PR number, short description, owner, commands to run, and estimated effort.

## Frontend tasks
- Task A1
  - PR: #33
  - Description: Validate axios upgrade (^1.6.0 -> ^1.12.2)
  - Owner: @repo-maintainer
  - Commands:
    - git fetch origin dependabot/npm_and_yarn/frontend/axios-tw-1.12.2:tmp/pr-33
    - git checkout tmp/pr-33
    - (cd frontend && npm ci && npm run build && npm test)
  - Estimated effort: 30–60 minutes

- Task A2
  - PR: #34
  - Description: Validate tailwindcss upgrade (3.x -> 4.x)
  - Owner: @repo-maintainer
  - Commands:
    - git fetch origin dependabot/npm_and_yarn/frontend/tailwindcss-4.1.13:tmp/pr-34
    - git checkout tmp/pr-34
    - (cd frontend && npm ci && npm run build && npm test)
    - Review Tailwind migration notes and search for deprecated classes
  - Estimated effort: 1–3 hours

- Task A3
  - PRs: #32, #31, #30
  - Description: Validate testing tool upgrades (ts-jest, jest, jest-dom)
  - Owner: @repo-maintainer
  - Commands:
    - git fetch origin dependabot/npm_and_yarn/frontend/ts-jest-tw-29.4.4:tmp/pr-32
    - git checkout tmp/pr-32
    - (cd frontend && npm ci && npm test)
  - Estimated effort: 30–90 minutes each

## Backend tasks
- Task B1
  - PR: #29
  - Description: Validate Gradle plugin bump (spotless 6 -> 7)
  - Owner: @repo-maintainer
  - Commands:
    - git fetch origin dependabot/gradle/backend/com.diffplug.spotless-7.2.1:tmp/pr-29
    - git checkout tmp/pr-29
    - ./gradlew clean build
    - ./gradlew spotlessApply
  - Estimated effort: 1–2 hours

## Workflow tasks
- Task W1
  - PRs: #28, #27, #26, #25
  - Description: Validate GitHub Actions upgrades (checkout v4→v5, setup-java v4→v5, github-script v7→v8)
  - Owner: DevOps or repo admin
  - Commands:
    - Create test branch merging the workflow update and trigger CI on GitHub (or run workflow locally via act)
    - Confirm runner compatibility for self-hosted runners (v2.327.1+ if required)
  - Estimated effort: 1–3 hours per workflow PR

## Merge plan
- Merge low-risk frontend PRs after green CI (axios, testing libs)
- Merge backend plugin after spotless/build pass
- Merge workflows last, watching CI carefully after each

## Notes
- Always create local backup branch before destructive ops:
  - git branch backup/master-$(date +%Y%m%d%H%M%S)

---
