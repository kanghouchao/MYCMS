
# バックエンド用 Docker Makefile
# ======================

# 変数定義
DOCKER_IMAGE ?= cms-backend
DOCKER_TAG ?= latest
REPORT_DIR ?= reports

# デフォルトターゲット
.DEFAULT_GOAL := help

help: ## ヘルプ情報を表示
	@echo "利用可能なコマンド一覧:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

test: ## バックエンドの全テストを実行し、backend/reports にレポートを収集
	@echo "🧪  バックエンドのテストを実行中..."
	@docker buildx build --load --target test -t $(DOCKER_IMAGE)-test:$(DOCKER_TAG) .
	@$(MAKE) test-copy-artifacts

test-copy-artifacts: ## テスト成果物を backend/reports にコピー
	@mkdir -p $(REPORT_DIR)/junit $(REPORT_DIR)/jacoco
	@set -e; \
		cid=$$(docker create $(DOCKER_IMAGE)-test:$(DOCKER_TAG) true); \
		echo "Copying test artifacts from container $$cid..."; \
		docker cp $$cid:/app/build/reports/tests/test $(REPORT_DIR)/junit || true; \
		docker cp $$cid:/app/build/reports/jacoco/test/html $(REPORT_DIR)/jacoco/html || true; \
		docker cp $$cid:/app/build/reports/jacoco/test/jacocoTestReport.xml $(REPORT_DIR)/jacoco/jacoco.xml || true; \
		docker rm -f $$cid >/dev/null 2>&1 || true

build: ## バックエンドDockerイメージをビルド
	@echo "🏗️  バックエンドDockerイメージをビルド中..."
	@docker buildx build -t $(DOCKER_IMAGE):$(DOCKER_TAG) .

clean: ## Dockerイメージを削除
	@echo "🗑️  バックエンドDockerイメージを削除中..."
	@docker rmi $(DOCKER_IMAGE):$(DOCKER_TAG) 2>/dev/null || echo "イメージが存在しないか、既に削除されています"
	@docker rmi $(DOCKER_IMAGE)-test:$(DOCKER_TAG) 2>/dev/null || echo "テストイメージが存在しないか、既に削除されています"

.PHONY: help build clean test
