# 仓库指南

这是使用Java和Next.js构建的多租户内容管理系统(CMS).
**一定要使用中文回复，写文档，创建PR等。**
**commit message should be in English.**

## 项目结构与模块组织
- `backend/` 实现 Spring Boot API（代码位于 `src/main/java`，配置位于 `src/main/resources`），单元测试放在 `src/test/java`。
- `frontend/` 包含 Next.js 应用；功能模块位于 `src/`，同路径的测试使用 `.test.tsx?` 后缀，静态资源放在 `public/`，管理端使用 CSR （在 `src/app/central` 中），客户端使用 SSR（在 `src/app/tenant` 中）。
- `environment/<env>/` 提供 Docker Compose 环境（`development`、`release`），并带有用于 `up`、`down`、`logs`、`exec` 的辅助 Makefile；CI 产物在流水线中会放在 `reports/`，并发布到github docs (https://kanghouchao.github.io/MYCMS/)。

## 构建、测试与开发命令
- `make build service=backend|frontend` 构建与生产运行时匹配的分阶段 Docker 镜像。
- `make test service=backend|frontend` 运行后端或前端测试。
- `make lint service=backend|frontend` 由 Spotless（后端）或 ESLint（前端）负责；使用 `make format service=frontend` 和 `make format service=backend` 修复格式问题。
- `make exec service=backend|frontend cmd="<command>"` 在对应容器内执行命令（例如 `bash`）。

## 代码风格与命名约定
- Java 使用 Spotless 强制 Google Java Format；包名保持小写（`com.cms.<module>`），DTO/Request 使用后缀，Lombok/Spring 注解遵循现有模式。
- TypeScript/React 使用 Prettier（2 空格缩进，单引号）和 `eslint.config.mjs`；组件/页面采用 PascalCase 命名，Hook 以 `use` 开头，服务客户端放在 `frontend/src/services`。
- 除非已有文件使用其他编码，否则新文件请保持 UTF-8。

## 测试指南
- 后端依赖 JUnit 且使用嵌入式 H2 配置；Jacoco 行覆盖率需达到 ≥70%。可通过 Docker（`make test service=backend`）或本地 Gradle 运行。
- 前端使用 Jest + Testing Library 和同路径测试；需保持 ≥70% 的覆盖率以满足阈值。
- 在触及租户路由、Cookie 或跨服务流程时，优先采用契约测试或集成测试。

## 提交与拉取请求指南
- 使用 Conventional Commits（`type(scope): summary`，不超过 70 字）并在适用时引用问题（`Fixes #<id>`）。
- PR 应说明目的与影响，突出关键文件，链接生成的报告，并记录人工验证步骤（例如 `make lint`、`make test`）。
- PR 创建应该参照模板（在`.github/pull_request_template.md`中）。
- 将基础设施调整与功能开发分开；明确列出后续跟进任务。

## 安全性、可观测性与配置
- 切勿提交真实的 `.env` 文件。
- 先写测试，再实现功能。
- 注意功能和文档的一致性。