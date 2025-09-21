# Copilot / AI Assistant Instructions for MYCMS

**This project uses Spec Kit for specification-driven development.**  
Before implementing any new feature or making major changes, ensure a spec / plan / tasks exist under `.spec-kit/`.

---

## High level project summary

- Project: MYCMS — multi-tenant CMS  
- Backend: Java 21 (formatting: Spotless + Google Java Format)  
- Frontend: Next.js + TypeScript  
- Dev infra: Docker Compose, Makefile (common commands: `make up`, `make test`, `make down`, etc.)

---

## MCP & AI usage (mandatory)

- **Use MCP where possible.** Prefer integrating AI assistants with the Model Context Protocol (MCP) so the assistant can access repository context and tooling in a controlled way.  
- **Use GitHub's MCP server for PR / remote repo operations.** For pull request reviews, comments, or reading repo content, prefer the GitHub MCP server connectors (use read-only variants when appropriate).  
- **Prefer the `sequential-thinking` MCP provider for deep, multi-step reasoning.** When a task requires step-by-step planning or iterative refinement, request the agent use a sequential-thinking MCP server or equivalent sequential reasoning provider.  
- **Security note:** MCP integrations provide powerful capabilities; prefer read-only or limited access connectors when possible and avoid exposing secrets or credentials to agents.

Branch naming handled by Spec Kit
--------------------------------

This project delegates feature-branch naming rules to Spec Kit. The helper function `check_feature_branch` in `.specify/scripts/bash/common.sh` enforces the convention: feature branches must begin with a three-digit numeric prefix followed by a hyphen (regexp: `^[0-9]{3}-`). If your branch does not match this pattern, Spec Kit scripts that rely on the helper will print an error and exit. When using Spec Kit tooling, create branches like `001-my-feature` to avoid failures.

> *These MCP / connector choices are required when available. If MCP integration is not available in the environment, the assistant should fallback to using `.spec-kit/` files and repository contents as the canonical context source.*

---

## Local command policy (critical)

- **The assistant MUST NOT run local commands on the user's machine.** If a suggested action requires running shell/Make/Docker commands, the assistant should:  
  1. Explain *why* the command is needed.  
  2. Provide **a step-by-step list** of exact commands the user should run locally.  
  3. Include any required environment variables, preconditions, and expected outputs.  
  4. Include cleanup or undo commands (how to revert) and safety checks (e.g., "ensure you are on feature branch and have committed local changes").  
  5. Explain how to verify success (what to look for in logs, exit codes, network ports, etc.).

### Example local-run instruction format (what assistant should provide)
1. Preconditions:
   - Ensure Git branch: `git status --porcelain` (expect: clean)  
   - Ensure Docker is running: `docker version` (expect: server & client info)  
2. Commands to run (copy & paste):
   ```bash
   # start dev environment
   make up

   # run unit tests (backend)
   make test service=backend

   # run frontend tests
   make test service=frontend
