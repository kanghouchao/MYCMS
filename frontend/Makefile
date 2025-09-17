
# フロントエンド用 Docker Makefile
# ======================

# 変数定義
DOCKER_IMAGE ?= cms-frontend
DOCKER_TAG ?= latest


# デフォルトターゲット
.DEFAULT_GOAL := help

help: ## ヘルプ情報を表示
	@echo "利用可能なコマンド一覧:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## フロントエンドDockerイメージをビルド
	@echo "🏗️  フロントエンドDockerイメージをビルド中..."
	@docker buildx build -t $(DOCKER_IMAGE):$(DOCKER_TAG) . --progress=plain

test: ## Docker コンテナ内でフロントエンドのユニットテストを実行（Dockerfile の test ステージを使用）
	@echo "Docker 上でテストを実行しています..."
	@docker buildx build --target test -t $(DOCKER_IMAGE)-test:$(DOCKER_TAG) .

clean: ## Dockerイメージを削除
	@echo "🗑️  フロントエンドDockerイメージを削除中..."
	@docker rmi $(DOCKER_IMAGE):$(DOCKER_TAG) 2>/dev/null || echo "イメージが存在しないか、既に削除されています"
	@docker rmi $(DOCKER_IMAGE)-test:$(DOCKER_TAG) 2>/dev/null || echo "テストイメージが存在しないか、既に削除されています"

.PHONY: help build test clean