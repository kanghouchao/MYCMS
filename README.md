# Oli CMS - 多租户内容管理系统

![Laravel](https://img.shields.io/badge/Laravel-11+-red.svg)
![Next.js](https://img.shields.io/badge/Next.js-14+-blue.svg)
![PHP](https://img.shields.io/badge/PHP-8.2+-blue.svg)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)
![Docker](https://img.shields.io/badge/Docker-latest-blue.svg)

一个基于 Laravel + Next.js 构建的现代化多租户内容管理系统，采用前后端分离架构和容器化部署。

## 📋 目录

- [项目概述](#-项目概述)
- [技术栈](#-技术栈)
- [架构设计](#-架构设计)
- [快速开始](#-快速开始)
- [核心功能](#-核心功能)
- [部署指南](#-部署指南)
- [开发指南](#-开发指南)
- [贡献指南](#-贡献指南)

## 🎯 项目概述

Oli CMS 是一个企业级的多租户内容管理系统，支持：

- **多租户架构**：每个租户拥有独立的数据空间和域名
- **前后端分离**：Laravel API + Next.js 前端
- **现代化 UI**：基于 Tailwind CSS 的响应式设计
- **完整的认证系统**：管理员和租户用户分离认证
- **Docker 化部署**：一键启动开发环境
- **可扩展架构**：支持水平扩展和容器化部署

## 🛠 技术栈

| 分类 | 技术选型 | 版本 | 备注 |
|------|----------|------|------|
| **后端框架** | Laravel | 11+ | Headless API，基于 Swoole 运行 |
| **前端框架** | Next.js | 14+ | React + TypeScript，静态构建 |
| **Web服务器** | Nginx | latest | 托管前端静态文件 |
| **数据库** | PostgreSQL | 16+ | 支持多租户数据隔离 |
| **缓存** | Redis | 7+ | 会话存储和缓存 |
| **反向代理** | Traefik | 3.5+ | 域名路由和负载均衡 |
| **容器化** | Docker | latest | 开发和生产环境 |
| **包管理器** | Composer | latest | PHP 依赖管理 |
| **包管理器** | npm/yarn | latest | Node.js 依赖管理 |
| **样式框架** | Tailwind CSS | 3.4+ | 原子化 CSS 框架 |
| **多租户** | stancl/tenancy | 3.9+ | Laravel 多租户包 |
| **API认证** | Laravel Sanctum | 4.0+ | SPA 认证 |

## 🏗 架构设计

### 整体架构

```text
                       ┌─────────────────┐
                       │   Frontend      │
                       │   Nginx         │
                  ┌───►│   HTML/JS/CSS   │
                  │    │   Port: 80      │
                  │    └─────────────────┘
┌─────────────────┐
│   Traefik       │        ┌─────────────────┐
│   (反向代理)    │◄───────►│   Backend       │
│   Port: 80      │        │   Laravel +     │
└─────────────────┘         │   Swoole        │
                            │   Port: 8000    │
                            └─────────────────┘
                                     │
                            ┌─────────────────┐
                            │   PostgreSQL    │
                            │   Database      │
                            │   Port: 5432    │
                            └─────────────────┘
```

**架构说明：**

- **前端**：Next.js 构建为静态文件（`output: "export"`），由 Nginx 提供服务
- **后端**：Laravel API 基于 Swoole 高性能运行时
- **代理**：Traefik 直接路由到前端静态资源和后端 API
- **数据**：PostgreSQL 提供多租户数据隔离

**请求流程：**

- 静态资源请求：`Browser → Traefik → Frontend (Nginx)`
- API 请求：`Browser → Traefik → Backend (Laravel + Swoole)`
- 数据库连接：`Backend → PostgreSQL`

**路由配置：**

- `oli-cms.test` → Frontend (Nginx 静态文件)
- `api.oli-cms.test` → Backend (Laravel API)

**架构特点：**

- **前后端分离**：前端静态化，后端 API 化
- **单体应用**：后端为单一 Laravel 应用，便于维护
- **容器化部署**：每个组件独立容器，易于扩展
- **多租户支持**：基于域名的租户隔离

### 多租户架构

```text
Central Application (管理后台)
├── Admin Authentication
├── Tenant Management
└── System Configuration

Tenant Applications (租户应用)
├── Domain-based Isolation
├── Independent Databases
└── Custom Configurations
```

### 项目结构

```text
oli-CMS/
├── backend/                # Laravel API 后端
│   ├── app/
│   │   ├── Http/Controllers/
│   │   │   ├── Admin/      # 管理员控制器
│   │   │   └── Api/        # API 控制器
│   │   ├── Models/         # 数据模型
│   │   └── Providers/      # 服务提供者
│   ├── config/             # 配置文件
│   ├── database/           # 数据库迁移和种子
│   ├── routes/             # 路由定义
│   │   ├── api.php         # API 路由
│   │   ├── web.php         # Web 路由
│   │   └── tenant.php      # 租户路由
│   └── vendor/             # Composer 依赖
├── frontend/               # Next.js 前端
│   ├── src/
│   │   ├── app/            # 应用页面
│   │   ├── components/     # 可复用组件
│   │   ├── contexts/       # React 上下文
│   │   ├── lib/            # 工具库
│   │   ├── services/       # API 服务
│   │   └── types/          # TypeScript 类型定义
│   ├── public/             # 静态资源
│   └── package.json        # 前端依赖
├── infrastructure/         # 基础设施代码
│   ├── main.tf             # Terraform 主配置
│   └── variables.tf        # 变量定义
├── traefik/               # 反向代理配置
│   ├── local/             # 本地开发配置
│   └── prod/              # 生产环境配置
├── docker-compose.yml     # Docker 编排文件
└── Makefile              # 项目管理命令
```

## 🚀 快速开始

### 环境要求

- Docker & Docker Compose
- Make (可选，用于快捷命令)

### 安装步骤

1. **克隆项目**

   ```bash
   git clone <repository-url>
   cd oli-CMS
   ```

2. **构建并启动服务**

   ```bash
   # 使用 Make 命令（推荐）
   make build  # 构建所有镜像
   make up     # 启动所有服务
   
   # 或使用 Docker Compose
   docker-compose up -d --build
   ```

3. **初始化数据库**

   ```bash
   # 运行数据库迁移
   make backend-migrate
   
   # 或手动执行
   docker-compose exec backend php artisan migrate
   
   # 播种初始数据
   docker-compose exec backend php artisan db:seed
   ```

4. **访问应用**
   - 管理后台: <http://oli-cms.test> 或 <http://localhost>
   - API 接口: <http://api.oli-cms.test>
   - Traefik 面板: <http://localhost:8080>

5. **默认管理员账户**
   - 超级管理员：`admin@cms.com` / `admin123`
   - 普通管理员：`user@cms.com` / `user123`

### 本地开发配置

如果你需要自定义域名，请将以下内容添加到 `/etc/hosts` 文件：

``` text
127.0.0.1 oli-cms.test
127.0.0.1 api.oli-cms.test
```

## 📁 核心功能

### 管理后台功能

- **🔐 管理员认证**
  - JWT Token 认证
  - 角色权限管理（超级管理员、普通管理员）
  - 账户状态控制

- **🏢 租户管理**
  - 创建、编辑、删除租户
  - 域名绑定管理
  - 套餐计划选择（基础版、高级版、企业版）
  - 租户状态监控

- **📊 仪表板**
  - 系统概览统计
  - 租户使用情况
  - 实时状态监控

### 多租户系统

- **🌐 域名隔离**
  - 每个租户独立域名
  - 自动路由识别
  - 防止跨租户访问

- **💾 数据隔离**
  - 租户数据库隔离
  - 文件存储隔离
  - 缓存空间隔离

- **⚙️ 配置隔离**
  - 租户独立配置
  - 自定义主题支持
  - 功能模块控制

## � 部署指南

## 🚀 部署指南

### 开发环境

1. 克隆项目并启动服务：

   ```bash
   git clone <repository-url>
   cd oli-CMS
   make up
   ```

2. 访问应用进行开发测试

### 生产环境

1. **环境变量配置**

   ```bash
   cp backend/.env.example backend/.env
   # 编辑 .env 文件，配置生产环境参数
   ```

2. **构建生产镜像**

   ```bash
   ENVIRONMENT=prod make build
   ```

3. **启动生产服务**

   ```bash
   ENVIRONMENT=prod docker-compose up -d
   ```

4. **SSL 证书配置**
   - 更新 `traefik/prod/traefik.yml` 配置
   - 配置 Let's Encrypt 或自签名证书

### 云平台部署

项目包含 Terraform 配置，支持 AWS 等云平台部署：

```bash
cd infrastructure
terraform init
terraform plan
terraform apply
```

## 💻 开发指南

### 后端开发

1. **代码规范**

   ```bash
   # 代码格式化
   docker-compose exec backend ./vendor/bin/pint
   
   # 运行测试
   docker-compose exec backend php artisan test
   ```

2. **数据库操作**

   ```bash
   # 创建迁移
   docker-compose exec backend php artisan make:migration create_example_table
   
   # 运行迁移
   docker-compose exec backend php artisan migrate
   
   # 创建模型
   docker-compose exec backend php artisan make:model Example
   ```

3. **多租户开发**

   ```bash
   # 创建租户迁移
   docker-compose exec backend php artisan make:migration create_tenant_posts_table
   
   # 为租户运行迁移
   docker-compose exec backend php artisan tenants:migrate
   ```

### 前端开发

1. **开发服务器**

   ```bash
   cd frontend
   npm run dev
   ```

2. **代码检查**

   ```bash
   npm run lint
   npm run type-check
   ```

3. **构建生产版本**

   ```bash
   npm run build
   npm run start
   ```

### 常用命令

使用项目根目录的 Makefile：

```bash
make help        # 显示所有可用命令
make up          # 启动所有服务
make down        # 停止所有服务
make restart     # 重启所有服务
make logs        # 查看服务日志
make ps          # 查看服务状态
make build       # 构建所有镜像
```

## 🤝 贡献指南

我们欢迎任何形式的贡献！

### 提交 Issue

- 使用 Issue 模板
- 提供详细的重现步骤
- 包含环境信息

### 提交 Pull Request

1. Fork 项目
2. 创建特性分支：`git checkout -b feature/amazing-feature`
3. 提交更改：`git commit -m 'Add amazing feature'`
4. 推送到分支：`git push origin feature/amazing-feature`
5. 开启 Pull Request

### 开发规范

- 遵循 PSR-12 代码规范（PHP）
- 使用 ESLint + Prettier（JavaScript/TypeScript）
- 编写测试用例
- 更新文档

## 📝 更新日志

### v1.0.0 (2024-01-01)

**新功能：**

- ✅ 多租户架构实现
- ✅ 管理员认证系统
- ✅ 租户管理功能
- ✅ Docker 化部署
- ✅ API 接口开发

**技术栈：**

- Laravel 11+ 后端 API (Swoole 运行时)
- Next.js 14+ 静态构建 + Nginx 服务
- PostgreSQL 16 数据库
- Traefik 3.5 反向代理
- Docker & Docker Compose

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

## 🙋‍♂️ 支持

如果你在使用过程中遇到问题，请：

1. 查看 [常见问题](docs/FAQ.md)
2. 搜索或提交 [Issues](issues)
3. 参考 [文档](docs/)

---

**作者：** [kanghouchao](https://github.com/kanghouchao)  
**项目地址：** [oli-CMS](https://github.com/kanghouchao/MYCMS)  
**最后更新：** 2024年8月6日
