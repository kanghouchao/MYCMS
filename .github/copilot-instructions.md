## Oli CMS — AI Coding Agent Guide

This repo is a split-architecture, multi-tenant CMS behind Traefik. Follow these project-specific patterns to be productive.

### Architecture
- Reverse proxy: `/api/*` → backend (prefix stripped), others → frontend (`docker-compose.yml`, `traefik/development/traefik.yml`).
- Frontend: Next.js 14 with middleware-based domain routing (`frontend/src/middleware.ts`).
- Backend: Spring Boot 3.5 (Java 21). Namespace endpoints under `/central/*` (admin) and `/tenant/*` (tenant). Health: `/actuator/health`.

### Multi-tenant flow & cookie contract
- Admin domains: `ADMIN_DOMAINS` in `frontend/src/middleware.ts` (default: `oli-cms.test`). Others treated as tenant.
- Tenant validation: middleware fetches `${TENANT_VALIDATION_API_URL || http://backend:8080/central/tenants}?domain=<host>`.
- Accepted response shapes: legacy `{ valid, template_key, tenant_id, tenant_name }` or current `{ id, name, domain, email }`.
- Cookies set: `x-mw-role` (`central|tenant`), `x-mw-tenant-template`, `x-mw-tenant-id`, `x-mw-tenant-name`.
- Server components must read via `cookies()`; root router (`frontend/src/app/page.tsx`) dynamically loads `tenant/templates/<template_key>/page` or redirects to `/central/dashboard`.

### Frontend patterns
- Use the shared axios client `frontend/src/lib/client.ts` (baseURL `/api`, sends `Authorization: Bearer <token>` from `js-cookie`; on 401 it clears token and redirects to `/login`). Do NOT create ad-hoc axios instances.
- Split API clients live in `frontend/src/services/central/api.ts` and `frontend/src/services/tenant/api.ts`; pick by domain via `AuthContext` (`NEXT_PUBLIC_CENTRAL_DOMAIN`).
- Add tenant templates under `frontend/src/app/tenant/templates/<key>/page.tsx`; ensure backend validation returns a matching `template_key`.

### Backend conventions
- Keep controller namespaces under `com.cms.controller.central` and `com.cms.controller.tenant`.
- All frontend requests arrive without the `/api` prefix; design mappings accordingly.
- Persisted IDs may use custom generators (e.g., `SnowflakeId`).

### Java coding guidelines
- Do not use `import *`: always explicitly import required classes (disable star imports in your IDE).
- Avoid fully qualified class names in code: import then use the simple class name (e.g., first `import java.time.Instant;` then write `Instant.now()`).
- Follow standard naming conventions: packages lowercase; Classes/Interfaces PascalCase; methods/fields camelCase; constants UPPER_SNAKE_CASE; test classes end with `Test`.
- Prefer modern Java features (Java 21): use Stream API for collection transformations, `Optional` to express absence, `switch` expressions and pattern matching; use parallel streams cautiously to avoid unnecessary overhead.
- Manage dependencies via the build tool: declare dependencies only in `backend/build.gradle`; do not commit or manually reference JARs; rely on Spring Boot dependency management and avoid pinning versions unless necessary.

### Dev workflow
- Build/run: `make build` then `make up` (starts Traefik, DB, Redis, backend, frontend). Logs: `make logs service=backend|frontend|traefik`.
- Tests: `make test` (all) or `make test service=backend|frontend`.
- Local domain switch: add `oli-cms.test` and a tenant host to `/etc/hosts`.

### Agent Execution: Avoiding Hung Terminals

Executing shell commands via an AI agent in VS Code can be unreliable, often leading to hung sessions. To ensure productivity and stability, follow these patterns:

**1. The Golden Rule: Use `Makefile` Targets**
- **Why**: Makefiles provide robust, reusable, and environment-agnostic scripts. They are the **most reliable** way to run multi-step tasks like builds, tests, and deployments.
- **How**: Instead of typing `npm run build && npm run test`, use `make ci-local` if it exists. If a suitable target doesn't exist, create one.
- **Example**:
  - **Bad (fragile)**: `docker-compose build backend && docker-compose build frontend`
  - **Good (robust)**: `make build`

**2. For Simple Tasks: Use Single-Shot Command Chains**
- **Why**: If a `Makefile` target is overkill, chain commands into a single line using `&&`. This avoids the overhead of multiple terminal sessions. Always start with `set -eo pipefail` (for `zsh`) or `set -euo pipefail` (for `bash`) to ensure the chain fails if any command fails.
- **How**: Use the pattern `bash -lc 'set -euo pipefail; command1 && command2'`.
- **Example (Git workflow)**:
  - `git checkout -b new-feature && git add . && git commit -m "feat: new feature" && git push -u origin new-feature`

**3. For Long-Running Processes: Start in Background**
- **Why**: Watchers, dev servers, and other long-running tasks will block the agent. Start them once as a background process.
- **How**: Use `make up` to start services and `make logs service=<name>` to monitor them, rather than running them directly in the agent's terminal session.

**4. Use Model Context Protocol (MCP) When Available**
- **Why**: MCP provides a structured, API-like way for the agent to perform tasks (e.g., interacting with GitHub, searching the web) without touching the shell. This is far more reliable than running `curl` or `gh` commands manually.
- **How**: If your editor supports MCP, enable the GitHub and other relevant servers. The agent should prefer these tools over raw terminal commands for supported operations.

**5. Background reliably with `nohup` and `&` (macOS zsh)**
- **Why**: Long-running tasks can block the agent; sessions may terminate and kill child processes. `&` sends tasks to the background; `nohup` keeps them alive after the session ends.
- **How**: Prefer Make targets first. When you must run a long task directly, use `nohup ... >/tmp/oli-<task>.log 2>&1 &` so output goes to a log file instead of `nohup.out`.
- **Examples (optional, adapt paths/names)**:
  - Start the stack without blocking the agent: `nohup make up >/tmp/oli-up.log 2>&1 &`
  - Run a longer local CI chain in background: `nohup bash -lc 'set -eo pipefail; make build && make test' >/tmp/oli-ci.log 2>&1 &`
- **Observe output**: Prefer `make logs service=backend|frontend|traefik` over tailing `nohup` logs when possible.


### Collaboration workflow (GitHub Flow)
- Start with an issue: track scope and acceptance; one PR per issue; link the issue in PR (e.g., "Closes #123").
- Branch from `master`: create a feature/fix branch, commit and push regularly.
- Open a PR against `master`: keep PRs small, add context and testing notes.
- Review then merge using Squash: address comments; when approved, ALWAYS squash-merge; delete the branch after merge.
- Keep `master` green: update your branch if out-of-date before merging.

### Solo development (pre-open-source)
- Even as a single maintainer, follow the same issue → branch → PR → squash flow to preserve reviewable history.
- In PR descriptions, include scope, risks, validation steps, and affected services (frontend/backend/infra).
- Follow the repository PR template `.github/pull_request_template.md`; fill in Quality Gates (build/lint/tests/coverage/smoke) and provide links or logs.
- For multi-file edits, add a short progress summary (what changed, why, and what's next) in the PR.
- Prefer small, incremental PRs and keep `master` deployable at all times.

### Key files
- Frontend: `src/middleware.ts`, `src/app/page.tsx`, `src/contexts/AuthContext.tsx`, `src/lib/client.ts`, `src/services/{central,tenant}/api.ts`, `src/app/tenant/templates/*`.
- Backend: `backend/src/main/java/com/cms/controller/{central,tenant}/`, `backend/src/main/resources/application.yml`.
- Infra: `docker-compose.yml`, `traefik/development/traefik.yml`, root `Makefile`.

Questions or unclear areas (e.g., tenant validation payloads, adding templates)? Note them in your PR and we’ll extend this guide.
