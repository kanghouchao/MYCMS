# Contracts — Dependabot workflow

Path: `/Users/kanghouchao/CodeProjects/cms/specs/001-dependabot-updates/contracts/README.md`

This directory contains the human-readable operational contract describing the expected inputs, outcomes, and safety checks for the dependabot rebase-and-merge workflow.

Key expectations:
- Input: GitHub PR authored by Dependabot, targeting `main`.
- Operator actions: rebase onto `main`, run tests, force-push, verify CI success, merge PR.
- Safety: CI must pass before merge; conflicts must be documented.

See `workflow.yaml` for a machine-friendly representation of the steps and checks.
