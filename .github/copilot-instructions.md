# Oli CMS - マルチテナント型CMSの開発ガイド

## プロジェクト概要
フロント/バック分離＋Dockerコンテナオーケストレーションによるマルチテナント型CMS。
管理者サイト(central)と各テナント独立サイトをホスト名で判別する単一フロントエンドアプリ。

**アーキテクチャの核心**:
- Spring Boot 3.5 (Java 21) + Next.js 14 のフル分離
- Traefikがルーティング: `/api/*` → backend、その他 → frontend
- マルチテナント: ホスト名ベースでcentral/tenant切り替え(`frontend/src/middleware.ts`)
- 認証: JWT stateless token、central/tenantで異なるAPI(`services/central/api.ts`, `services/tenant/api.ts`)

## ディレクトリ構造＆責務

```
├── backend/                     # Spring Boot API
│   └── src/main/java/com/cms/
│       ├── controller/
│       │   ├── central/        # 管理者API (tenant管理等)
│       │   └── tenant/         # テナント専用API  
│       ├── model/              # JPA Entity
│       └── service/            # ビジネスロジック
├── frontend/                   # Next.js アプリ
│   └── src/
│       ├── middleware.ts       # ホスト名判定＆ルーティング
│       ├── app/
│       │   ├── central/        # 管理者UI (oli-cms.test)
│       │   ├── tenant/         # テナントUI (各独自ドメイン)
│       │   └── login/          # 共通ログイン
│       ├── contexts/AuthContext.tsx  # central/tenant判定認証
│       └── services/           # API クライアント分離
└── traefik/                    # 逆プロキシ設定
```

## 重要な開発パターン

### 1. マルチテナント切り替え
```typescript
// frontend/src/middleware.ts - ドメイン判定ロジック
const ADMIN_DOMAINS = new Set(["oli-cms.test"]);
// oli-cms.test → central管理画面、その他 → tenant画面
```

### 2. API分離パターン
```typescript
// services/central/api.ts vs services/tenant/api.ts
## Oli CMS — AI Coding Agent Guide

This repo is a multi-tenant CMS with a split architecture: Spring Boot backend and Next.js frontend, orchestrated behind Traefik. These notes capture the project-specific patterns you must follow to be productive.

### Architecture in one glance
- Reverse proxy: Traefik routes `/api/*` to backend, everything else to frontend (`docker-compose.yml`, `traefik/development/traefik.yml`). Backend sees paths without `/api` due to a stripPrefix middleware.
- Frontend: Next.js 14 app with middleware-based domain routing (`frontend/src/middleware.ts`). Admin domains → central app; other domains → tenant app.
- Backend: Spring Boot 3.5 (Java 21). Namespace your endpoints under `/central/*` (admin) and `/tenant/*` (tenant). Health: `/actuator/health`.
- Data: PostgreSQL + Redis (configured via env; see `backend/src/main/resources/application.yml`).

### Multi-tenant flow and cookies contract
- Domain switch: `ADMIN_DOMAINS` in `frontend/src/middleware.ts` (defaults to `oli-cms.test`).
- Tenant validation: middleware calls `${TENANT_VALIDATION_API_URL || http://backend:8080/central/tenants}?domain=<host>` and expects JSON `{ valid, template_key, tenant_id, tenant_name }`.
- Cookies set by middleware:
	- `x-mw-role`: `central | tenant`
	- `x-mw-tenant-template`: template key for SSR
	- `x-mw-tenant-id`, `x-mw-tenant-name`: tenant meta
- Root routing (`frontend/src/app/page.tsx`): loads `tenant/templates/<template_key>/page` dynamically or redirects to `/central/dashboard` when role is `central`.
- Important: In server components, read these via `cookies()` (not raw `headers()`), since middleware writes cookies.

### API client and auth patterns
- Always use the shared axios client `frontend/src/lib/client.ts` (baseURL `/api`, attaches `Authorization: Bearer <token>` from `js-cookie`, 401 → clear token and redirect `/login`). Do not create ad-hoc axios instances.
- Split API surface:
	- Central: `frontend/src/services/central/api.ts` (e.g., `/central/login`, `/central/tenants`, `/central/tenants/stats`).
	- Tenant: `frontend/src/services/tenant/api.ts` (e.g., `/tenant/register`, `/tenant/login`).
- Choose auth API by domain using `AuthContext` (`frontend/src/contexts/AuthContext.tsx`): `NEXT_PUBLIC_CENTRAL_DOMAIN` defines the central host; others treated as tenant.

### Templates and UX isolation
- Tenant templates live under `frontend/src/app/tenant/templates/<key>/page.tsx`.
- To add a new template:
	1) Create the folder and a `page.tsx` export.
	2) Ensure tenant validation returns `template_key` matching the folder name.
	3) Middleware will set `x-mw-tenant-template`; the root page will resolve and render it.

### Local development & ops
- Build & run with Docker:
	- `make build` → builds backend and frontend images (`cms-backend`, `cms-frontend`).
	- `make up` → starts Traefik, DB, Redis, backend, frontend; health checks gate readiness.
	- `make logs service=backend|frontend|traefik` for focused logs; `make ps` for status.
- Tests:
	- `make test` (all), or `make test service=backend|frontend` (runs Dockerfile test stages).
- Hosts setup (for domain switching): add `oli-cms.test` and a tenant domain to `/etc/hosts`.

### Conventions that matter here
- Use English for code, comments, and docs. Keep API paths under `/central/*` and `/tenant/*` consistently.
- All frontend API traffic must go through `/api` so Traefik routes it to backend and strips the prefix.
- Do not bypass the middleware for tenant context; rely on the cookie contract above.
- When changing the central domain, update both `ADMIN_DOMAINS` (middleware) and `NEXT_PUBLIC_CENTRAL_DOMAIN` (AuthContext).

### Key files for orientation
- Frontend: `src/middleware.ts`, `src/app/page.tsx`, `src/contexts/AuthContext.tsx`, `src/lib/client.ts`, `src/services/{central,tenant}/api.ts`, `src/app/tenant/templates/*`.
- Backend: `src/main/java/com/cms/controller/{central,tenant}/`, `application.yml`.
- Infra: `docker-compose.yml`, `traefik/development/traefik.yml`, top-level `Makefile`.

If any section is unclear (e.g., tenant validation response shape or adding new templates), leave a short note in your PR and we’ll extend this guide.
