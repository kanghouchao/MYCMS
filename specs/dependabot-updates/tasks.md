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
```markdown
# Tasks: Dependabot updates remediation

**Feature dir**: `/Users/kanghouchao/CodeProjects/cms/specs/dependabot-updates`
**Input**: `/Users/kanghouchao/CodeProjects/cms/specs/dependabot-updates/plan.md`
**Goal**: Triage, validate, and safely merge Dependabot PRs #34..#25 with deterministic, executable steps.

## Quick summary
- Source plan: `/Users/kanghouchao/CodeProjects/cms/specs/dependabot-updates/plan.md`
- Available design docs: only `plan.md` present (no data-model.md, no contracts/, no research.md, no quickstart.md)
- Approach: TDD-like triage (tests & CI) before merging; minimize blast radius by area.

---

## Numbering & rules used
- IDs: T001, T002, ...
- [P] marks tasks that can run in parallel (different files / no shared resources).
- Tests (triage + CI validation) MUST run before any merge tasks.

---

## Phase 1 — Setup (prereqs)
- [X] T001 Ensure GitHub CLI (`gh`) is available and authenticated (repository root)
  - File(s): `/usr/bin/gh` (system path) — verify installed
  - Commands (execute from repository root `/Users/kanghouchao/CodeProjects/cms`):
    ```bash
    gh auth status || gh auth login
    gh --version
    ```

Notes: If `gh` is unavailable, the tasks provide equivalent curl/REST alternatives in the command notes below.

---

## Phase 2 — Triage (Tests first)  (TESTS MUST PASS BEFORE MERGING)
Rules applied: frontend PRs all change the same file path `frontend/package.json` so treat frontend triage tasks as sequential (no [P] among them). Backend and workflow PRs touch different areas and can run in parallel with frontend triage.

- [X] T002 Triage PR #33 — axios upgrade (frontend)
  - Affected path: `/Users/kanghouchao/CodeProjects/cms/frontend/package.json`
  - Purpose: run install, build, tests, lint for frontend to detect breakage from axios ^1.6.0 → ^1.12.2
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    git fetch origin dependabot/npm_and_yarn/frontend/axios-tw-1.12.2:tmp/pr-33
    git checkout tmp/pr-33
    (cd frontend && npm ci)
    (cd frontend && npm run build)
    (cd frontend && npm test -- -i)
    (cd frontend && npm run lint)
    ```
  - Outcome: comment `triage/ok` or `triage/requires-manual-review` on PR #33

- T003 Triage PR #34 — tailwindcss 3.x → 4.x (frontend)
  - Affected path: `/Users/kanghouchao/CodeProjects/cms/frontend/package.json`
  - Purpose: run install/build/tests and validate Tailwind migration notes
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    git fetch origin dependabot/npm_and_yarn/frontend/tailwindcss-4.1.13:tmp/pr-34
    git checkout tmp/pr-34
    (cd frontend && npm ci)
    (cd frontend && npm run build)
    # Run project-specific Tailwind migration checks (search for deprecated utilities/styles)
    (cd frontend && npm test -- -i)
    ```
  - Note: If migration is non-trivial, mark `triage/requires-manual-review` and open an issue documenting required UI changes.

- T004 Triage PR #32 — ts-jest (frontend)
  - Affected path: `/Users/kanghouchao/CodeProjects/cms/frontend/package.json`
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    git fetch origin dependabot/npm_and_yarn/frontend/ts-jest-tw-29.4.4:tmp/pr-32
    git checkout tmp/pr-32
    (cd frontend && npm ci)
    (cd frontend && npm test -- -i)
    ```

- T005 Triage PR #31 — jest (frontend)
  - Affected path: `/Users/kanghouchao/CodeProjects/cms/frontend/package.json`
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    git fetch origin dependabot/npm_and_yarn/frontend/jest-30.1.3:tmp/pr-31
    git checkout tmp/pr-31
    (cd frontend && npm ci)
    (cd frontend && npm test -- -i)
    ```

- T006 Triage PR #30 — @testing-library/jest-dom (frontend)
  - Affected path: `/Users/kanghouchao/CodeProjects/cms/frontend/package.json`
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    git fetch origin dependabot/npm_and_yarn/frontend/testing-library/jest-dom-tw-6.8.0:tmp/pr-30
    git checkout tmp/pr-30
    (cd frontend && npm ci)
    (cd frontend && npm test -- -i)
    ```

- T007 [P] Triage PR #29 — com.diffplug.spotless Gradle plugin bump (backend)
  - Affected path: `/Users/kanghouchao/CodeProjects/cms/backend/build.gradle`
  - Purpose: verify Gradle plugin compatibility and run build/spotless checks
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    git fetch origin dependabot/gradle/backend/com.diffplug.spotless-7.2.1:tmp/pr-29
    git checkout tmp/pr-29
    (cd backend && ./gradlew clean build --no-daemon)
    (cd backend && ./gradlew spotlessCheck)
    # If spotlessCheck fails and modifies files, run:
    (cd backend && ./gradlew spotlessApply && git add -A && git commit -m "chore: apply spotless fixes for plugin upgrade")
    ```
  - Outcome: build+spotlessCheck pass → triage/ok; otherwise open issue describing changes and fixes.

- T008 [P] Triage PR #28 — actions/checkout v4→v5 (workflows)
  - Affected path(s): `/Users/kanghouchao/CodeProjects/cms/.github/workflows/*`
  - Purpose: validate workflow run behavior on GitHub-hosted or self-hosted runners
  - Commands (create test branch to trigger workflows on GitHub):
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    git fetch origin dependabot/github_actions/actions/checkout-5:tmp/pr-28
    git checkout tmp/pr-28
    git checkout -b tmp/ci-test-28
    git push -u origin tmp/ci-test-28
    # Then re-run workflows on GitHub for this branch and confirm runner compatibility
    ```

- T009 [P] Triage PR #27 — actions/checkout duplicate (workflows)
  - Same commands as T008 but replace branch refs for PR #27

- T010 [P] Triage PR #26 — actions/setup-java v4→v5 (workflows)

- T011 [P] Triage PR #25 — actions/github-script v7→v8 (workflows)

Notes: T007–T011 (backend & workflows) can run in parallel with frontend triage tasks (T002–T006) because they touch different paths.

---

## Phase 3 — CI Validation (after triage passes)
- T012 [P] Re-run CI / trigger workflow dispatch for triaged PR branches
  - Purpose: ensure build, tests, lint, and security checks (CodeQL) pass in CI
  - Example commands (GitHub CLI):
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    # For PR 33 as an example
    gh pr checkout 33 || (git fetch origin dependabot/npm_and_yarn/frontend/axios-tw-1.12.2:tmp/pr-33 && git checkout tmp/pr-33)
    gh pr status --show mergeable
    # Use workflow dispatch or GitHub UI to re-run jobs as needed
    ```

Dependency: T012 requires the corresponding triage task (T002..T011) to be completed and marked triage/ok.

---

## Phase 4 — Merge (only after triage + CI green)
Rules: Do not merge multiple PRs that edit the same file (`frontend/package.json`) in parallel — merge sequentially to avoid conflicts. Backend and workflow merges may be done independently but monitor CI after each.

- T013 Merge PR #33 (axios) — only after T002 and T012 for #33 are OK
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    # Preferred via GitHub CLI
    gh pr merge 33 --squash --delete-branch
    # or comment to request Dependabot to merge: gh pr comment 33 --body "@dependabot squash and merge"
    ```

- T014 Merge PR #32 (ts-jest) — after T004 & CI
- T015 Merge PR #31 (jest) — after T005 & CI
- T016 Merge PR #30 (jest-dom) — after T006 & CI
- T017 Merge PR #34 (tailwindcss) — after T003 & CI and migration review

- T018 Merge PR #29 (spotless) — after T007 & CI (spotlessCheck/build pass)
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    gh pr checkout 29 || (git fetch origin dependabot/gradle/backend/com.diffplug.spotless-7.2.1:tmp/pr-29 && git checkout tmp/pr-29)
    (cd backend && ./gradlew clean build)
    gh pr merge 29 --squash --delete-branch
    ```

- T019 Merge workflow PRs (#28, #27, #26, #25) — merge one at a time, monitor master CI after each
  - Commands (example for PR 28):
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    gh pr merge 28 --squash --delete-branch
    # Wait for master CI and quick smoke tests to pass before merging the next workflow PR
    ```

Notes: Because frontend package updates touch the same file, merge T013–T017 sequentially. Backend and workflows can be merged when their own CI passes.

---

## Phase 5 — Post-merge monitoring & rollback
- T020 Monitor `master` CI and CodeQL for 60 minutes after each merge
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    gh run list --repo $GITHUB_REPOSITORY --limit 20
    # Optionally: use gh run watch <run-id> to observe a run
    ```

- T021 Revert problematic merge (if regression discovered)
  - Commands:
    ```bash
    cd /Users/kanghouchao/CodeProjects/cms
    git checkout master
    git pull origin master
    # Replace <merge-sha> with the merge commit SHA to revert
    git revert <merge-sha> -m 1
    git push origin HEAD:master
    gh pr create --base master --head $(git rev-parse --abbrev-ref HEAD) --title "revert: <merge-sha>" --body "Revert due to regression"
    ```

---

## Parallel execution examples
- Safe parallel groups (can run concurrently):
  - Group P1: T007, T008, T009, T010, T011 (backend & workflow triage) — different repository areas
  - Group P2: T012 when targeting different PR branches that don't touch the same files

- Sequential-only group (do NOT parallelize): T002, T003, T004, T005, T006 (frontend triage & merges) because they modify `/frontend/package.json`.

Example: run backend triage + frontend triage in parallel on separate runners
```bash
# Runner A (frontend sequential triage)
./scripts/run_task.sh T002 && ./scripts/run_task.sh T003 && ./scripts/run_task.sh T004 && ./scripts/run_task.sh T005 && ./scripts/run_task.sh T006

# Runner B (backend + workflows triage in parallel)
./scripts/run_task.sh T007 &
./scripts/run_task.sh T008 &
./scripts/run_task.sh T009 &
./scripts/run_task.sh T010 &
./scripts/run_task.sh T011 &
wait
```

Notes: `./scripts/run_task.sh` is an example helper wrapper that runs the listed commands for a task; create it if you want uniform logging and status reporting.

---

## Acceptance criteria (mapped to the original plan)
- All listed PRs (#34..#25) are either merged or marked `triage/requires-manual-review` with an attached issue explaining blockers.  (T013–T019)
- CI (build/test/lint/CodeQL) stays green on `master` for at least 60 minutes after merges. (T020)
- If regressions occur, revert commits and open a revert PR. (T021)

---

## Files changed / paths used in tasks (absolute)
- `/Users/kanghouchao/CodeProjects/cms/frontend/package.json` — touched by PRs 33,34,32,31,30
- `/Users/kanghouchao/CodeProjects/cms/backend/build.gradle` — touched by PR 29
- `/Users/kanghouchao/CodeProjects/cms/.github/workflows/` — touched by PRs 25–28

---

## Next steps for an LLM/agent to execute these tasks
1. Run T001 locally to ensure `gh` is present.
2. Execute frontend triage tasks in sequence (T002→T006). Record outcomes and post PR comments.
3. In parallel, run backend + workflow triage (T007→T011). Record outcomes.
4. Trigger CI validation (T012) for branches marked `triage/ok`.
5. Merge PRs per rules (T013→T019), monitoring CI after each.
6. Perform post-merge monitoring (T020) and revert if necessary (T021).

---

Generated: 2025-09-20
Source: `/Users/kanghouchao/CodeProjects/cms/specs/dependabot-updates/plan.md`

``` 
