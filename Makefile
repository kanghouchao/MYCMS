
# CMS Project Makefile
# ======================
env ?= development
service ?=
USE_CACHE_EXPORT ?= 0

help: ## ヘルプ情報を表示
	@echo "Oli CMS プロジェクト管理コマンド:"
	@echo ""
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""

build: ## Dockerイメージをビルド
ifdef service
	@make -C $(service) build
else
	@make -C backend build
	@make -C frontend build
endif

lint: ## すべてのサービスのLintを実行（service=frontend|backend で個別指定可）
ifdef service
	@echo "🧹 $(service) の Lint を実行中..."
	@make -C $(service) lint USE_CACHE_EXPORT=$(USE_CACHE_EXPORT)
else
	@echo "🧹 すべてのサービスの Lint を実行中..."
	@echo "🧹 Frontend lint..."
	@make -C frontend lint USE_CACHE_EXPORT=$(USE_CACHE_EXPORT)
	@echo "🧹 Backend lint..."
	@make -C backend lint USE_CACHE_EXPORT=$(USE_CACHE_EXPORT)
	@echo "✅ Lint 完了"
endif

test: ## すべてのサービスのテストを実行（frontend/coverage & backend/reports に収集）
ifdef service
	@echo "🔍 $(service)サービスのテストを実行中..."
	@make -C $(service) test USE_CACHE_EXPORT=$(USE_CACHE_EXPORT)
else
	@echo "🔍 すべてのサービスのテストを実行中..."
	@echo "🧪 Frontend tests..."
	@make -C frontend test USE_CACHE_EXPORT=$(USE_CACHE_EXPORT)
	@echo "🧪 Backend tests..."
	@make -C backend test USE_CACHE_EXPORT=$(USE_CACHE_EXPORT)
	@echo "✅ Done. frontend/coverage と backend/reports を参照してください。"
endif

format: ## すべてのサービスのフォーマットを実行（service=frontend|backend で個別指定可）
ifdef service
	@echo "✍️  $(service) のフォーマットを実行中..."
	@make -C $(service) format
else
	@echo "✍️  すべてのサービスのフォーマットを実行中..."
	@echo "✍️  Frontend format..."
	@make -C frontend format
	@echo "✍️  Backend format..."
	@make -C backend format
	@echo "✅ Format 完了"
endif

up: ## サービスを起動
	@echo "🚀 サービスを起動中..."
	@make -C environment/$(env) up

down: ## サービスを停止
	@echo "🛑 サービスを停止中..."
	@make -C environment/$(env) down

ps: ## サービスの状態を表示
	@make -C environment/$(env) ps

logs: ## すべてのサービスのログを表示
ifndef service
	@make -C environment/$(env) logs
else
	echo "📜 $(service)サービスのログを表示中...";
	@make -C environment/$(env) logs service=$(service);
endif

exec: ## サービスコンテナに入る
ifdef service
	@make -C environment/$(env) exec service=$(service);
endif

clean: ## 未使用のイメージとコンテナをクリーンアップ
ifndef service
	@make -C backend clean
	@make -C frontend clean
	@docker system prune -f
else
	@make -C $(service) clean
endif

restart: ## サービスを再起動
ifndef service
	@echo "🔄 すべてのサービスを再起動中..."
	@make -C environment/$(env) restart
else
	@echo "🔄 $(service)サービスを再起動中..."
	@make -C environment/$(env) restart service=$(service)
endif

.PHONY: help build test up down ps logs exec clean restart
