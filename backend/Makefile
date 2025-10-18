
# バックエンド用 Docker Makefile
# ======================

# 変数定義
DOCKER_IMAGE ?= cms-backend
DOCKER_TAG ?= latest
REPORT_DIR ?= ../reports/

# Cache configuration (only enabled when USE_CACHE_EXPORT=1, for CI)
USE_CACHE_EXPORT ?= 0
CACHE_DIR ?= /tmp/.buildx-cache-backend
CACHE_ARGS =
ifeq ($(USE_CACHE_EXPORT),1)
	CACHE_ARGS = --cache-from=type=local,src=$(CACHE_DIR) --cache-to=type=local,dest=$(CACHE_DIR),mode=max
endif

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

lint: ## コードスタイルを検査（Docker lint ステージ経由）
	@echo "🧹 Docker の lint ステージでコードスタイル検査を実行中..."
	@docker buildx build --load --target lint \
		$(CACHE_ARGS) \
		-t $(DOCKER_IMAGE)-lint:$(DOCKER_TAG) .

format: ## コードスタイルを自動修正（Spotless apply; ローカルで gradlew 実行）
	@echo "✍️  Spotless で自動整形を実行中..."
	@$(GRADLEW) --no-daemon --warning-mode=all spotlessApply

test: ## バックエンドの全テストを実行し、reports/backend に収集
	@echo "🧪  バックエンドのテストを実行中..."
	@docker buildx build --load --target test \
		$(CACHE_ARGS) \
		-t $(DOCKER_IMAGE)-test:$(DOCKER_TAG) .
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
