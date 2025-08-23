
# Oli CMS - マルチテナント型コンテンツ管理システム

![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5+-green.svg)
![Next.js](https://img.shields.io/badge/Next.js-14+-blue.svg)
![Java](https://img.shields.io/badge/Java-21+-blue.svg)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)
![Docker](https://img.shields.io/badge/Docker-latest-blue.svg)

Spring Boot + Next.js をベースにしたモダンなマルチテナント型コンテンツ管理システムです。フロントエンド・バックエンド分離、Docker・Traefikによるコンテナオーケストレーションを採用しています。

## 📋 目次

- [概要](#概要)
- [技術スタック](#技術スタック)
- [アーキテクチャ](#アーキテクチャ)
- [クイックスタート](#クイックスタート)
- [ディレクトリ構成](#ディレクトリ構成)
- [サポート](#サポート)

## 🎯 概要

Oli CMS は以下の特徴を持つエンタープライズ向けマルチテナントCMSです：

- **マルチテナント**：各テナントが独立したデータベース・ドメインを持つ
- **フロント/バック分離**：Spring Boot API + Next.js フロントエンド
- **モダンUI**：Tailwind CSS によるレスポンシブデザイン
- **管理者認証**：JWTベースのAPI認証
- **コンテナ構成**：Docker / Traefik による容易な起動・運用
- **水平スケール**：サービス分離 & ステートレス設計

## 🛠 技術スタック

| カテゴリ | 技術 | バージョン | 用途 |
|----------|------|-----------|------|
| バックエンド | Spring Boot | 3.5+ | REST APIサーバ |
| フロントエンド | Next.js | 14+ | SSR/CSR対応Webアプリ |
| 言語 | Java | 21+ | バックエンド実装 |
| DB | PostgreSQL | 16+ | テナント毎のデータ隔離 |
| キャッシュ | Redis | 7+ | キャッシュ・セッション管理 |
| 逆プロキシ | Traefik | 3.5+ | ルーティング・L7制御 |
| コンテナ | Docker | latest | 本番/開発環境統一 |
| スタイル | Tailwind CSS | 3.4+ | ユーティリティCSS |
| マルチテナント | 独自実装/拡張 | - | テナント管理 |
| 認証 | JWT + Stateless | - | API認証 |

## 🏗 アーキテクチャ

### 全体構成

```text
                        ┌─────────────────┐
                        │   Frontend      │
                        │   Next.js (Node)│
           ┌───────────►│   Port: 3000    │
           │            └────────────────┘
┌─────────────────┐
│   Traefik       │        ┌─────────────────┐
│   (ReverseProxy)│◄────────►│   Backend       │
│   Port: 80      │        │   Spring Boot    │
   └─────────────────┘         │   Port: 8080    │
                            └─────────────────┘
                                     │
                            ┌─────────────────┐
                            │   PostgreSQL    │
                            │   Redis         │
                            └─────────────────┘
```

- **Frontend**: Next.js による動的Webアプリ
- **Backend**: Spring Boot によるREST API
- **Routing**: Traefikが `/api/*` をバックエンド、その他をフロントエンドへ振り分け
- **Data Layer**: PostgreSQL + Redis

## 🚀 クイックスタート

### 必要環境

- Docker & Docker Compose
- Make

### セットアップ手順

1. **リポジトリのクローン**

   ```bash
   git clone https://github.com/kanghouchao/MYCMS.git
   cd oli-CMS
   ```

2. **サービス起動**

   ```bash
   make build up
   ```

3. **ローカルドメイン設定**

   `/etc/hosts` に以下を追加してください：

   ``` text
   127.0.0.1 oli-cms.test
   127.0.0.1 tenant.domain.example.com  # 例
   ```

4. **Make コマンド一覧**

   ```bash
   make help
   ```

## 📁 ディレクトリ構成

```text
oli-CMS/
├── backend/                # Spring Boot バックエンド
│   ├── src/
│   │   ├── main/java/      # Java ソース
│   │   ├── main/resources/ # 設定・マイグレーション
│   │   └── test/           # テストコード
│   ├── build.gradle        # Gradle 設定
│   └── Dockerfile          # バックエンド用Docker
├── frontend/               # Next.js フロントエンド
│   ├── src/                # ページ・コンポーネント等
│   ├── public/             # 静的リソース
│   └── package.json        # 依存管理
├── traefik/                # Traefik 設定
│   ├── local/              # 開発用
│   └── prod/               # 本番用
├── docker-compose.yml      # コンテナ定義
└── Makefile                # 管理コマンド
```

## 🙋‍♂️ サポート

ご質問・不具合報告は以下をご利用ください：

- [GitHub Issues](https://github.com/kanghouchao/MYCMS/issues)

---

**作者:** [kanghouchao](https://github.com/kanghouchao)  
**リポジトリ:** [oli-CMS](https://github.com/kanghouchao/MYCMS)  
**最終更新:** 2025-08-23

---

## 🚀 クイックスタート

### 必要環境

- Docker & Docker Compose
- Make

### セットアップ手順

1. **リポジトリのクローン**

   ```bash
   git clone https://github.com/kanghouchao/MYCMS.git
   cd oli-CMS
   ```

2. **サービス起動**

   ```bash
   make build up
   ```

3. **ローカルドメイン設定**

   `/etc/hosts` に以下を追加してください：

   ``` text
   127.0.0.1 oli-cms.test
   127.0.0.1 tenant.domain.example.com  # 例
   ```

4. **Make コマンド一覧**

   ```bash
   make help
   ```

## 📁 ディレクトリ構成

```text
oli-CMS/
├── backend/                # Spring Boot バックエンド
│   ├── src/
│   │   ├── main/java/      # Java ソース
│   │   ├── main/resources/ # 設定・マイグレーション
│   │   └── test/           # テストコード
│   ├── build.gradle        # Gradle 設定
│   └── Dockerfile          # バックエンド用Docker
├── frontend/               # Next.js フロントエンド
│   ├── src/                # ページ・コンポーネント等
│   ├── public/             # 静的リソース
│   └── package.json        # 依存管理
├── traefik/                # Traefik 設定
│   ├── local/              # 開発用
│   └── prod/               # 本番用
├── docker-compose.yml      # コンテナ定義
└── Makefile                # 管理コマンド
```

## 🙋‍♂️ サポート

ご質問・不具合報告は以下をご利用ください：

- [GitHub Issues](https://github.com/kanghouchao/MYCMS/issues)

---
   cd oli-CMS
   ```

2. **サービス起動**

   ```bash
   make build up
   ```

### ローカルドメイン設定

``` text
もしあなたがカスタムドメインを設定する必要がある場合は、以下の内容を `/etc/hosts` ファイルに追加してください：

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


