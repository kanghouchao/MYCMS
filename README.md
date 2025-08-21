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

### プロジェクト構成

```text
oli-CMS/
├── backend/                # Laravel API バックエンド
│   ├── artisan
│   ├── composer.json
│   ├── app/
│   │   ├── Exceptions/        # アプリケーション例外
│   │   ├── Guards/            # 認証ガード
│   │   ├── Http/
│   │   │   ├── Controllers/
│   │   │   │   ├── Central/      # 管理者用コントローラー
│   │   │   │   └── Tenant/       # テナント用コントローラー
│   │   │   └── Middleware/    # ミドルウェア
│   │   ├── Models/         # データモデル
│   │   ├── Utils/          # ユーティリティ
│   │   └── Providers/      # サービスプロバイダー
│   ├── config/             # 設定ファイル
│   ├── database/           # マイグレーション・シーダー
│   ├── routes/             # ルーティング定義
│   │   ├── api.php         # APIルート
│   │   ├── web.php         # Webルート
│   │   └── tenant.php      # テナントルート
│   └── vendor/             # Composer依存
├── frontend/               # Next.js フロントエンド
│   ├── src/
│   │   ├── app/            # アプリページ
│   │   ├── components/     # 再利用可能コンポーネント
│   │   ├── contexts/       # Reactコンテキスト
│   │   ├── lib/            # ライブラリ
│   │   ├── services/       # APIサービス
│   │   └── types/          # TypeScript型定義
│   ├── public/             # 静的リソース
│   └── package.json        # フロント依存
├── traefik/                # 逆プロキシ設定
│   ├── local/              # ローカル開発用
│   └── prod/               # 本番環境用
├── docker-compose.yml      # Docker構成ファイル
└── Makefile                # プロジェクト管理コマンド
```

上記は本プロジェクトのディレクトリ構成です。
各フォルダの役割は以下の通りです：

- `backend/`：LaravelベースのAPIサーバ。認証・テナント管理・DB操作などを担当。
- `frontend/`：Next.jsによる管理画面・テナント画面。SSR/CSR両対応。
- `traefik/`：Traefikの設定ファイル。リバースプロキシ・ルーティング制御。
- `docker-compose.yml`：全サービスのコンテナ定義。
- `Makefile`：開発・運用コマンド集。

## 🚀 クイックスタート

### 必要環境

- Docker & Docker Compose
- Make

### セットアップ手順

1. **克隆项目**

   ```bash
   git clone https://github.com/kanghouchao/MYCMS.git
   cd oli-CMS
   ```

2. **サービス起動**

   ```bash
   # 使用 Make 命令
   make build up
   ```

### ローカルドメイン設定

如果你需要自定义域名，请将以下内容添加到 `/etc/hosts` 文件：

``` text
127.0.0.1 oli-cms.test
127.0.0.1 tenant.domain.example.com  ## 例
```

### Make コマンド

```bash
make help        # 全てのコマンドを表示
```

**技術内訳:**

- Laravel 11+ 后端 API (Swoole 运行时)
- Next.js 14+ Node.js ランタイム実行 (Middleware 対応)
- PostgreSQL 16 数据库
- Traefik 3.5 反向代理
- Docker & Docker Compose

## 🙋‍♂️ サポート

もし質問や問題がある場合は、以下のリソースを参照してください：

1. [GitHub Issues](https://github.com/kanghouchao/MYCMS/issues)

---

**作者:** [kanghouchao](https://github.com/kanghouchao)  
**リポジトリ:** [oli-CMS](https://github.com/kanghouchao/MYCMS)  
**最終更新:** 2025-08-21
