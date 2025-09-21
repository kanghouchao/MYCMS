# Quickstart — Operator steps (manual workflow)

Absolute path: `/Users/kanghouchao/CodeProjects/cms/specs/001-dependabot-updates/quickstart.md`

Preconditions:
- Ensure Git working tree is clean: `git status --porcelain` (expect empty)
- Ensure you are on the coordinator branch: `git branch --show-current` should be `001-dependabot-updates`
- Ensure you have push rights to the dependabot branches (or be a maintainer)
- Ensure Docker (if using), Java/Node toolchains available locally for running tests

Operator flow (macOS, zsh):

1. Fetch latest main and PR branches

```bash
# fetch main and remote branches
git fetch origin main
git fetch origin "refs/pull/*/head:refs/remotes/origin/pr/*"
```

2. For each Dependabot PR branch (example `dependabot/npm_and_yarn/foo-1.2.3`):

```bash
# create a local branch tracking the PR head
git checkout -b dependabot-PR-<NUM> origin/<dependabot-branch>

# rebase onto latest main
git rebase origin/main

# if conflicts appear: resolve files, then
git add <resolved-files>
git rebase --continue

# run tests locally (backend + frontend as applicable)
# Backend (Java/Gradle)
./gradlew test

# Frontend (optional)
cd frontend && npm ci && npm test

# if tests pass: force-push the rebased branch
git push --force-with-lease origin HEAD:<dependabot-branch>

# merge the PR via GitHub UI or CLI (example using gh)
gh pr merge <PR_NUMBER> --admin --squash --delete-branch

# cleanup local branch
git checkout 001-dependabot-updates
git branch -D dependabot-PR-<NUM>
```

Verification:
- After force-push, confirm CI status on GitHub is `success` before merging.
- If CI fails, document the failure on the PR and skip or request maintainer intervention.

Conflict guidance:
- If rebase produces complex conflicts that cannot be resolved quickly, post a comment on the PR describing the blockers and skip to next PR (see FR-003 in spec.md).

Undo / safety checks:
- To revert a force-push (if needed) you can use the original head SHA if recorded:

```bash
# restore branch to previous commit (example)
git push --force origin <original-sha>:<dependabot-branch>
```

Notes:
- Commands above are operator-oriented. If automation is desired later, implement scripts with exact equivalence and add test coverage + CI gates before enabling bot-driven force-pushes.
