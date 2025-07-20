
# Gemini 项目架构与进度文档

本文档旨在记录 `oli-CMS` 项目的核心技术架构、关键决策和当前开发进度，以便于 Gemini AI 代理在未来的开发中能够快速理解上下文并提供精准的帮助。

*最后更新于: 2025-07-20*

## 1. 项目核心目标

构建一个支持**多租户 (Multi-tenancy)**、**前后端分离 (Headless)** 的现代化内容管理系统 (CMS)。

## 2. 技术栈概览 (Tech Stack)

| 分类 | 技术选型 | 备注 |
| :--- | :--- | :--- |
| **后端 (Backend)** | Laravel 11+ | 作为 Headless API |
| **前端 (Frontend)** | Next.js (React + TypeScript) | 负责所有 UI 渲染 |
| **数据库 (Database)** | PostgreSQL 12+ | |
| **API 风格 (API Style)** | RESTful | |
| **反向代理/网关** | Traefik | 本地开发使用 HTTP |
| **本地开发环境** | Docker & Docker Compose | |
| **基础设施代码化 (IaC)** | Terraform | 占位符已创建 |
| **云平台 (Cloud)** | AWS (Amazon Web Services) | |

## 3. 项目结构

采用 Monorepo 结构，各部分职责清晰：

```
/
├── backend/          # Laravel App (API Only)
├── frontend/         # Next.js App
├── infrastructure/   # Terraform IaC
├── docker-compose.yml # Root Docker orchestrator
├── traefik/          # Traefik configuration
└── README.md
```

## 4. 当前进度与关键配置

### 4.1. 环境搭建

- [X] 使用 Docker Compose 完成本地开发环境搭建。
- [X] 所有服务 (traefik, backend, frontend, database) 均已成功启动。
- [X] 本地开发环境配置为 **纯 HTTP** 模式，方便调试。
- [X] Traefik 仪表盘位于 `http://localhost:8080`。
- [X] 前端服务通过 `http://localhost` (或 `http://oli-cms.test`) 访问。
- [X] 后端服务通过 `http://api.oli-cms.test` 访问。

### 4.2. 后端 (Laravel)

- [X] 项目已创建，并移除了与前端相关的多余文件 (`package.json`, `vite.config.js` 等)。
- [X] `.env` 文件已配置为连接到 Docker 中的 PostgreSQL 数据库。
- [X] **多租户包 `stancl/tenancy` 已成功安装并初始化**。
- [X] 已运行数据库迁移，创建了 `tenants` 和 `domains` 表。

### 4.3. 前端 (Next.js)

- [X] 由于 `create-next-app` 的交互问题，项目文件已**手动创建**，确保了项目的可用性。
- [X] 包含 `package.json`, `tsconfig.json`, `next.config.mjs` 等核心文件。
- [X] 基础页面 `src/app/page.tsx` 已创建并可以成功访问。

## 5. 下一步计划

1.  创建第一个租户及其关联域名。
2.  在 `routes/tenant.php` 中定义租户专属的 API 路由。
3.  测试并验证多租户的域名识别和数据隔离功能。

---
*该文档由 Gemini AI 代理创建和维护。*
