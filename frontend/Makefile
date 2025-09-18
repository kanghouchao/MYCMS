
# フロントエンド用 Docker Makefile
# ======================

# 変数定義
DOCKER_IMAGE ?= cms-frontend
DOCKER_TAG ?= latest
COVERAGE_DIR ?= coverage


# デフォルトターゲット
.DEFAULT_GOAL := help

help: ## ヘルプ情報を表示
	@echo "利用可能なコマンド一覧:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## フロントエンドDockerイメージをビルド
	@echo "🏗️  フロントエンドDockerイメージをビルド中..."
	@docker buildx build -t $(DOCKER_IMAGE):$(DOCKER_TAG) .

test: ## Docker コンテナ内でユニットテストを実行し、artifacts/ にレポートを収集
	@echo "Docker 上でテストを実行しています..."
	@docker buildx build --load --target test -t $(DOCKER_IMAGE)-test:$(DOCKER_TAG) .
	@$(MAKE) test-copy-artifacts

test-copy-artifacts: ## テスト成果物を frontend/coverage と frontend/junit.xml にコピー
	@rm -rf $(COVERAGE_DIR) && mkdir -p $(COVERAGE_DIR)
	@cid=$(shell docker create $(DOCKER_IMAGE)-test:$(DOCKER_TAG) true) && \
		docker cp $$cid:/app/coverage $(COVERAGE_DIR) || true && \
		docker cp $$cid:/app/junit.xml $(COVERAGE_DIR)/junit.xml || true && \
		docker rm -f $$cid >/dev/null 2>&1 || true

clean: ## Dockerイメージを削除
	@echo "🗑️  フロントエンドDockerイメージを削除中..."
	@docker rmi $(DOCKER_IMAGE):$(DOCKER_TAG) 2>/dev/null || echo "イメージが存在しないか、既に削除されています"
	@docker rmi $(DOCKER_IMAGE)-test:$(DOCKER_TAG) 2>/dev/null || echo "テストイメージが存在しないか、既に削除されています"

.PHONY: help build test clean