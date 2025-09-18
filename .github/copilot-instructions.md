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

### Dev workflow
- Build/run: `make build` then `make up` (starts Traefik, DB, Redis, backend, frontend). Logs: `make logs service=backend|frontend|traefik`.
- Tests: `make test` (all) or `make test service=backend|frontend`.
- Local domain switch: add `oli-cms.test` and a tenant host to `/etc/hosts`.

### VS Code AI agent execution tips (terminal + MCP)
- Single-shot terminal runs: prefer chaining commands in one invocation or using Makefile targets to avoid repeated terminal sessions hanging in some agent modes.
	- For zsh: use `&&` to chain and enable safer flags at the start: `set -euo pipefail` when using bash -lc.
	- Prefer Makefile targets for multi-step flows (build, lint, test) instead of issuing many commands from the agent.
- Recommended patterns
	- One-shot commit/push (example):
		- `git checkout -b chore/update-copilot-instructions && git add .github/copilot-instructions.md && git commit -m "docs: update Copilot instructions (agent & MCP)" && git push -u origin chore/update-copilot-instructions`
	- One-shot local CI run via Make:
		- `make test && make lint || true` (adjust to your targets; prefer a single Make target like `make ci-local` that runs build/lint/test together)
- Long-running tasks: start once in background (watch/dev servers) and interact via logs rather than restarting.
- When an action would require multiple sequential terminal calls, either:
	1) create or reuse a Make target (e.g., `make ci-local`, `make release`), or
	2) use a single `bash -lc 'set -euo pipefail; cmd1 && cmd2 && cmd3'`.

### Model Context Protocol (MCP) usage guidelines
- Purpose: MCP servers let the AI agent perform multi-step or remote tasks (GitHub issues/PRs, web research, repo IO) more reliably than ad-hoc terminal calls.
- Recommended servers to enable (when available in your editor):
	- GitHub server: create/update issues, PRs, reviews, labels.
	- Web/content server (e.g., crawl/scrape): gather documentation or changelogs to support PRs.
	- Filesystem/repository helpers: structured read/write and searches.
- Installation/enablement: if your editor supports MCP, enable relevant servers in the AI/agent settings and authenticate as required. The agent should proactively remind you to install useful servers when a task would benefit from them.
- Security: never exfiltrate secrets; prefer repository or organization-level tokens with least privilege; keep tokens out of commit history and logs.
- Fallbacks: if MCP is unavailable, prefer Make targets and single-shot terminal invocations to keep interactions stable.

### Collaboration workflow (GitHub Flow)
- Start with an issue: track scope and acceptance; one PR per issue; link the issue in PR (e.g., "Closes #123").
- Branch from `master`: create a feature/fix branch, commit and push regularly.
- Open a PR against `master`: keep PRs small, add context and testing notes.
- Review then merge using Squash: address comments; when approved, ALWAYS squash-merge; delete the branch after merge.
- Keep `master` green: update your branch if out-of-date before merging.

### Solo development (pre-open-source)
- Even as a single maintainer, follow the same issue → branch → PR → squash flow to preserve reviewable history.
- In PR descriptions, include scope, risks, validation steps, and affected services (frontend/backend/infra).
- Prefer small, incremental PRs and keep `master` deployable at all times.

### Key files
- Frontend: `src/middleware.ts`, `src/app/page.tsx`, `src/contexts/AuthContext.tsx`, `src/lib/client.ts`, `src/services/{central,tenant}/api.ts`, `src/app/tenant/templates/*`.
- Backend: `backend/src/main/java/com/cms/controller/{central,tenant}/`, `backend/src/main/resources/application.yml`.
- Infra: `docker-compose.yml`, `traefik/development/traefik.yml`, root `Makefile`.

Questions or unclear areas (e.g., tenant validation payloads, adding templates)? Note them in your PR and we’ll extend this guide.
