# Phase 1 — Data Model

Path: `/Users/kanghouchao/CodeProjects/cms/specs/001-dependabot-updates/data-model.md`

## Key Entities

1) DependabotPR
- id: string (GitHub PR number)
- branch: string (e.g., `dependabot/npm_and_yarn/...`)
- title: string
- author: string (dependabot[bot])
- base: string (`main`)
- head_sha: string (commit SHA)
- status: enum {open, closed, merged}
- ci_status: enum {pending, success, failure}
- last_checked: timestamp

2) CoordinatorBranch
- name: string (e.g., `001-dependabot-updates`)
- purpose: string
- created_at: timestamp
- closed_at: timestamp

## Validation Rules
- Only process PRs where author is Dependabot (or matches configured allowlist).
- After rebase and force-push, CI must reach `success` state before merging unless an exception is approved and recorded.

## State Transitions (high level)
1. open -> rebased (after rebase & force-push)
2. rebased -> ci_success OR ci_failure
3. ci_success -> merged
4. ci_failure -> skipped / requires operator remediation
