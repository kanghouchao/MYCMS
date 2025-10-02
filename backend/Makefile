
# バックエンド用 Docker Makefile
# ======================

# 変数定義
DOCKER_IMAGE ?= cms-backend
DOCKER_TAG ?= latest
REPORT_DIR ?= ../reports/

# デフォルトターゲット
.DEFAULT_GOAL := help

# Use the Gradle wrapper; on Windows use gradlew.bat
ifeq ($(OS),Windows_NT)
	GRADLEW = gradlew.bat
else
	GRADLEW = ./gradlew
endif

help: ## ヘルプ情報を表示
	@echo "利用可能なコマンド一覧:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

database: ## データベースコンテナを起動
	@echo "🚀  データベースコンテナを起動中..."
	@docker run -d --name cms-local-db -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=KIDYpWwxi -e POSTGRES_DB=cms -p 5432:5432 postgres:16-alpine
	@echo "データベースコンテナが起動しました。"
	@echo "接続情報: host=localhost port=5432 dbname=cms user=admin password=KIDYpWwxi"
	@echo "🚀  Redisコンテナを起動中..."
	@docker run -d --name cms-local-redis -p 6379:6379 redis:8.2.0-alpine3.22
	@echo "Redisコンテナが起動しました。"
	@echo "接続情報: host=localhost port=6379"

stop-database: ## データベースコンテナを停止・削除
	@echo "🛑  データベースコンテナを停止・削除中..."
	@docker rm -f cms-local-db 2>/dev/null || echo "データベースコンテナが存在しないか、既に停止されています"
	@docker rm -f cms-local-redis 2>/dev/null || echo "Redisコンテナが存在しないか、既に停止されています"

lint: ## コードスタイルを検査（Docker lint ステージ経由 - Checkstyle）
	@echo "🧹 Docker lint ステージで Checkstyle を実行中..."
	@docker buildx build --load --target lint -t $(DOCKER_IMAGE)-lint:$(DOCKER_TAG) .

format: ## 自動整形はサポート外（Checkstyle only）
	@echo "ℹ️  Checkstyle は整形を行いません。IDE等で手動整形してください。"

test: ## バックエンドの全テストを実行し、reports/backend に収集
	@echo "🧪  バックエンドのテストを実行中..."
	@docker buildx build --load --target test -t $(DOCKER_IMAGE)-test:$(DOCKER_TAG) .
	@$(MAKE) test-copy-artifacts

test-copy-artifacts: ## テスト成果物を REPORT_DIR/backend にコピー
	@rm -rf $(REPORT_DIR)/backend
	@set -e; \
		cid=$$(docker create $(DOCKER_IMAGE)-test:$(DOCKER_TAG) true); \
		docker cp $$cid:/app/build/reports $(REPORT_DIR)/backend || true; \
		docker rm -f $$cid >/dev/null 2>&1 || true

build: ## バックエンドDockerイメージをビルド
	@echo "🏗️  バックエンドDockerイメージをビルド中..."
	@docker buildx build -t $(DOCKER_IMAGE):$(DOCKER_TAG) .

clean: ## Dockerイメージを削除
	@echo "🗑️  バックエンドDockerイメージを削除中..."
	@docker rmi $(DOCKER_IMAGE):$(DOCKER_TAG) 2>/dev/null || echo "イメージが存在しないか、既に削除されています"
	@docker rmi $(DOCKER_IMAGE)-test:$(DOCKER_TAG) 2>/dev/null || echo "テストイメージが存在しないか、既に削除されています"
	@docker rmi $(DOCKER_IMAGE)-lint:$(DOCKER_TAG) 2>/dev/null || echo "Lintイメージが存在しないか、既に削除されています"

.PHONY: help build clean test
