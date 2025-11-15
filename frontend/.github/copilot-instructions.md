## 目的
このプロジェクトで AI コーディングエージェントが即戦力になるための要点をまとめる。具体的なファイル例、実行コマンド、設計上の判断基準に焦点を当てる。

## ビッグピクチャ（アーキテクチャ）
- **Next.js フロントエンド**: `src/app` 下にページがあり、`next` による SSR/SSG とクライアントコンポーネント混在の構成。
- **マルチテナント対応**: middleware (`src/middleware.ts`) がホスト名を検証し、`x-mw-role`／`x-mw-tenant-*` のクッキーをセットすることで「central」(管理者) と「tenant」(テナント) を判別する。
- **API 層の分離**: `src/services/central/api.ts` と `src/services/tenant/api.ts` にそれぞれの API 呼び出しをまとめ、`src/lib/client.ts` の axios インスタンスを使う。

## 重要なファイルと役割（参照例）
- `src/middleware.ts` — ドメイン検証、`x-mw-role`/`x-mw-tenant-*` クッキーを設定する。ログは `console.error` で出力される。
- `src/lib/client.ts` — 共通 axios クライアント。リクエストで `token`、`XSRF-TOKEN`、`x-mw-*` クッキーをヘッダに付与。401 はログアウトへリダイレクトする。
- `src/services/*/api.ts` — API 呼び出しの集中ポイント（例: `centralApi.getList`, `authApi.login`）。
- `src/contexts/AuthContext.tsx` — `isTenantDomain()` を使いテナント/セントラルで適切な auth API を選ぶ。`token` の存在で認証をチェック。
- `src/lib/config.ts` — 中央ドメイン判定（`NEXT_PUBLIC_CENTRAL_DOMAIN` 環境変数）

## 実行・開発フロー（コマンド）
- ローカル開発: `npm run dev`（Next.js の `next dev`）
- ビルド: `npm run build`（`next build`）
- テスト: `npm test`（Jest）または `npm run test:watch`
- Lint/Format: `npm run lint` / `npm run format` / `npm run format:check`

## プロジェクト固有の慣習と注意点
- **Role/ tenant 情報はミドルウェア由来**: フロント側は cookie に頼ってロール/テナント ID を読み取り、API リクエストヘッダに `X-Role` / `X-Tenant-ID` を付与する。
- **中央 vs テナント API の切替**: `isTenantDomain()`（`src/lib/config.ts`）が主な判断基準。`AuthContext` や UI の分岐はこの関数に依存している。
- **ミドルウェアの外部依存**: テナント検証に `TENANT_VALIDATION_API_URL` 環境変数（デフォルト `http://backend:8080/central/tenants`）を使う。エラー時は 403 を返す挙動があるため要注意。
- **ログ出力は `console.error`**: `middleware.ts` はデバッグ用に詳細なログを出す。変更時はログ内容と副作用を確認する。

## テスト・モックのポイント
- ナビゲーションは `src/lib/navigation.ts` の `__setNavigatorForTests` で差し替え可能。jsdom 環境向けに try/catch がある。
- axios を使った API 呼び出しは `apiClient` をモックするか `msw` のようなツールでエンドポイントを模擬するのが簡単。

## 典型的な変更フローのヒント
- 新しい API を追加する場合は `src/services/<central|tenant>/api.ts` に関数を追加し、型は `src/types/api.ts` を参照して追加する。
- テナント固有 UI を追加する場合は `src/app/tenant/templates/*` 配下のテンプレートとミドルウェアのクッキー値（`x-mw-tenant-template`）を使う。

## 環境変数（見えているもの）
- `NEXT_PUBLIC_CENTRAL_DOMAIN` — セントラルのホスト名（デフォルト `oli-cms.test`）
- `TENANT_VALIDATION_API_URL` — ミドルウェアが呼ぶテナント検証 API

## コーディングスタイル / 静的検査
- ESLint が設定されており、`npm run lint` を実行すること。Prettier での整形は `npm run format`。

## まとめ（短く）
1. ドメイン判定は `middleware.ts` と `isTenantDomain()` に依存する。これが central/tenant の振る舞いを決める主要な「契約」。
2. API 呼び出しは `src/services` を通す。認証・CSRF・role/tenant は `src/lib/client.ts` が統一して付与する。
3. 開発は `npm run dev`／テストは `npm test`。ミドルウェアは起動中に詳細ログを出すのでデバッグが容易。

---
フィードバックがあれば中国語で教えてください。必要なら説明を追記・例示します。
