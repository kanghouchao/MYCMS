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
// AuthContextでホスト名判定し適切なAPIを選択
const getAuthApi = () => (isTenantDomain() ? tenantAuthApi : centralAuthApi);
```

### 3. ルーティング戦略
- Traefik: `/api/*` は必ずbackendへ
- Next.js middleware: ホスト名でcentralアプリかtenantアプリか判定
- 同一コードベースで完全に独立したUX

## 開発ワークフロー

### 起動手順
```bash
make build              # 全サービスビルド
make up                 # Docker Compose起動
make logs service=backend  # 個別ログ確認
```

### テスト
```bash
make test               # 全テスト実行
make test service=backend  # バックエンドのみ
```

### ローカル開発設定
`/etc/hosts` に追加:
```
127.0.0.1 oli-cms.test              # 管理者サイト
127.0.0.1 tenant.example.com        # テナントサイト例
```

## コーディング規約

- **言語**: 日本語コメント・ログ、英語コード・DB
- **認証**: JWT token、stateless設計
- **エラーハンドリング**: Spring Boot例外→日本語ログ、フロント→toast通知
- **CSS**: Tailwind CSS utility-first、responsive考慮
