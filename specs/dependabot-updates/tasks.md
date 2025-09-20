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
