# Oli CMS - マルチ店舗コンテンツ管理システム

![Laravel](https://img.shields.io/badge/Laravel-11+-red.svg)
![Next.js](https://img.shields.io/badge/Next.js-14+-blue.svg)
![PHP](https://img.shields.io/badge/PHP-8.2+-blue.svg)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)
![Docker](https://img.shields.io/badge/Docker-latest-blue.svg)

Laravel + Next.js をベースにしたモダンなマルチ店舗（マルチテナント）型コンテンツ管理システム。フロント/バックエンド分離と Docker / Traefik によるコンテナオーケストレーションを採用しています。

## 📋 目次

- [概要](#-概要)
- [技術スタック](#-技術スタック)
- [アーキテクチャ](#-アーキテクチャ)
- [クイックスタート](#-クイックスタート)
- [主な機能](#-主な機能)
- [デプロイ](#-デプロイ)
- [開発ガイド](#-開発ガイド)
- [コントリビュート方法](#-コントリビュート方法)

## 🎯 概要

Oli CMS は以下を特徴とするエンタープライズ志向のマルチ店舗 CMS です：

- **マルチ店舗（テナント）**：各店舗が独立したデータ空間とドメインを持つ
- **フロント/バック分離**：Backend(Laravel API) + Frontend(Next.js)
- **モダン UI**：Tailwind CSS によるレスポンシブ
- **管理者認証**：JWT ベースの管理 API
- **コンテナ構成**：Docker / Traefik による簡易起動
- **水平スケール容易**：サービス分離 & Stateless 設計

## 🛠 技術スタック

| 分类 | 技术选型 | 版本 | 备注 |
|------|----------|------|------|
| カテゴリ | 技術 | バージョン | 用途 |
|----------|------|-----------|------|
| Backend | Laravel | 11+ | Headless API (Swoole ランタイム) |
| Frontend | Next.js | 14+ | SSR/Middleware 対応ランタイム実行 |
| DB | PostgreSQL | 16+ | マルチ店舗データ隔離 |
| Cache | Redis | 7+ | キャッシュ & セッション |
| 逆プロキシ | Traefik | 3.5+ | ルーティング & L7 制御 |
| コンテナ | Docker | latest | 本番/開発統一化 |
| PHP 依存 | Composer | 2.x | ライブラリ管理 |
| Node 依存 | npm | 10+ | Frontend 依存管理 |
| スタイル | Tailwind CSS | 3.4+ | ユーティリティ CSS |
| マルチ店舗 | stancl/tenancy | 3.9+ | テナント管理 |
| 認証 | JWT + Stateless 中間層 | - | 管理者 API 認証 |

## 🏗 アーキテクチャ

### 全体構成

```text
                        ┌─────────────────┐
                        │   Frontend      │
                        │   Next.js (Node)│
           ┌───────────►│   Runtime App   │
           │            │   Port: 3000    │
           │            └────────────────┘
┌─────────────────┐
│   Traefik       │        ┌─────────────────┐
│   (反向代理)    │◄────────►│   Backend       │
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

**構成ポイント：**

- **Frontend**: Next.js を Node ランタイムで動的実行（Middleware 利用可能）
- **Backend**: Laravel (Swoole) による長常駐・高性能 HTTP サーバ
- **Routing**: Traefik が `/api/*` を backend、その他を frontend へ動的振り分け
- **Data Layer**: PostgreSQL + Redis（将来 S3 等の外部ストレージ拡張想定）

**リクエストフロー：**

- フロント配信: `Browser → Traefik → Frontend (Next.js 3000)`
- API: `Browser → Traefik → Backend (Swoole 8000)`
- DB/Cache: `Backend → PostgreSQL / Redis`

**Traefik ルール（簡略）:**

| 条件 | 宛先サービス |
|------|--------------|
| PathPrefix(`/api/`) | backend(8000) |
| その他 | frontend(3000) |

**特徴:**

- 明確な境界（/api 経由で統一）
- Stateless スケール（Frontend/Backend 水平展開容易）
- テナント解決はドメインベース（DB + キャッシュ）
- Swoole により低レイテンシ/常駐実行

### マルチ店舗構造

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

### プロジェクト構成

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

## 🚀 クイックスタート

### 必要環境

- Docker & Docker Compose
- Make (可选，用于快捷命令)

### セットアップ手順

1. **克隆项目**

   ```bash
   git clone <repository-url>
   cd oli-CMS
   ```

2. **サービス起動**

   ```bash
   # 使用 Make 命令（推荐）
   make build  # 构建所有镜像
   make up     # 启动所有服务
   
   # 或使用 Docker Compose
   docker-compose up -d --build
   ```

3. **データベース初期化**

   ```bash
   # 运行数据库迁移
   make backend-migrate
   
   # 或手动执行
   docker-compose exec backend php artisan migrate
   
   # 播种初始数据
   docker-compose exec backend php artisan db:seed
   ```

4. **アクセス**

   - 管理 UI: [http://localhost](http://localhost) （または hosts 設定後 [http://oli-cms.test](http://oli-cms.test)）
   - API: [http://localhost/api](http://localhost/api) 以下
   - Traefik Dashboard: [http://localhost:8080](http://localhost:8080)

5. **初期管理者アカウント（例）**
   - Super Admin: `admin@cms.com` / `admin123`
   - General Admin: `user@cms.com` / `user123`

### ローカルドメイン設定

如果你需要自定义域名，请将以下内容添加到 `/etc/hosts` 文件：

``` text
127.0.0.1 oli-cms.test
127.0.0.1 api.oli-cms.test
```

## 📁 主な機能

### 管理機能

#### 🔐 管理者認証

- JWT ベース / Stateless
- ロール（Super / Admin）
- アカウント有効/無効

#### 🏢 店舗管理

- 作成 / 更新 / 削除
- ドメイン紐付け
- プラン (basic / premium / enterprise)
- 状態表示

#### 📊 ダッシュボード

- 全体店舗カウント
- 最近作成一覧
- （将来）利用状況指標

### マルチ店舗システム

#### 🌐 ドメイン分離

- 独立ドメイン単位解決
- Traefik + アプリ層キャッシュ
- クロステナント防止

#### 💾 データ分離

- （現在）ロジカル分離（同一 DB 内テーブル）
- 将来: 物理 DB / スキーマ分離拡張可能
- キャッシュタグスコープ

#### ⚙️ 設定/拡張

- data JSON 属性への柔軟メタ格納
- 将来: テーマ / モジュール切替

## 🚀 デプロイ

### 開発環境

1. 克隆项目并启动服务：

   ```bash
   git clone <repository-url>
   cd oli-CMS
   make up
   ```

2. 访问应用进行开发测试

### 本番環境

1. **環境変数設定**

   ```bash
   cp backend/.env.example backend/.env
   # 编辑 .env 文件，配置生产环境参数
   ```

2. **本番ビルド**

   ```bash
   ENVIRONMENT=prod make build
   ```

3. **本番起動**

   ```bash
   ENVIRONMENT=prod docker-compose up -d
   ```

4. **SSL/TLS**
   - `traefik/prod/traefik.yml` に ACME 設定追加
   - Let’s Encrypt / 企業証明書対応

### インフラ (Terraform)

项目包含 Terraform 配置，支持 AWS 等云平台部署：

```bash
cd infrastructure
terraform init
terraform plan
terraform apply
```

## 💻 開発ガイド

### Backend 開発

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

3. **マルチ店舗（テナント）開発**

   ```bash
   # 创建店铺（租户）迁移（仍使用 tenants 命令空间）
   docker-compose exec backend php artisan make:migration create_shop_posts_table
   
   # 为所有店铺运行迁移（包内命令名仍为 tenants:migrate）
   docker-compose exec backend php artisan tenants:migrate
   ```

### Frontend 開発

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

### Make コマンド

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

## 🤝 コントリビュート方法

我们欢迎任何形式的贡献！

### Issue 提出

- 使用 Issue 模板
- 提供详细的重现步骤
- 包含环境信息

### Pull Request 手順

1. Fork 项目
2. 创建特性分支：`git checkout -b feature/amazing-feature`
3. 提交更改：`git commit -m 'Add amazing feature'`
4. 推送到分支：`git push origin feature/amazing-feature`
5. 开启 Pull Request

### コーディング規約

- 遵循 PSR-12 代码规范（PHP）
- 使用 ESLint + Prettier（JavaScript/TypeScript）
- 编写测试用例
- 更新文档

## 📝 更新履歴

### v1.0.0 (2024-01-01)

**追加機能:**

- ✅ 多租户架构实现
- ✅ 管理员认证系统
- ✅ 租户管理功能
- ✅ Docker 化部署
- ✅ API 接口开发

**技術内訳:**

- Laravel 11+ 后端 API (Swoole 运行时)
- Next.js 14+ Node.js ランタイム実行 (Middleware 対応)
- PostgreSQL 16 数据库
- Traefik 3.5 反向代理
- Docker & Docker Compose

## 📄 ライセンス

本项目采用 [MIT License](LICENSE) 许可证。

## 🙋‍♂️ サポート

如果你在使用过程中遇到问题，请：

1. 查看 [常见问题](docs/FAQ.md)
2. 搜索或提交 [Issues](issues)
3. 参考 [文档](docs/)

---

**作者:** [kanghouchao](https://github.com/kanghouchao)  
**リポジトリ:** [oli-CMS](https://github.com/kanghouchao/MYCMS)  
**最終更新:** 2025-08-08
