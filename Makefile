
# CMS Project Makefile
# ======================

help: ## ヘルプ情報を表示
	@echo "Oli CMS プロジェクト管理コマンド:"
	@echo ""
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""

.PHONY: help build test up down ps logs exec clean restart

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
	@make -C $(service) lint
else
	@echo "🧹 すべてのサービスの Lint を実行中..."
	@echo "🧹 Frontend lint..."
	@make -C frontend lint
	@echo "🧹 Backend lint..."
	@make -C backend lint
	@echo "✅ Lint 完了"
endif

test: ## すべてのサービスのテストを実行（frontend/coverage & backend/reports に収集）
ifdef service
	@echo "🔍 $(service)サービスのテストを実行中..."
	@make -C $(service) test
else
	@echo "🔍 すべてのサービスのテストを実行中..."
	@echo "🧪 Frontend tests..."
	@make -C frontend test
	@echo "🧪 Backend tests..."
	@make -C backend test
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
	@docker-compose up -d --timestamps --wait

down: ## サービスを停止
	@echo "🛑 サービスを停止中..."
	@docker-compose down

ps: ## サービスの状態を表示
	@docker-compose ps

logs: ## すべてのサービスのログを表示
ifndef service
	@docker-compose logs -f;
else
	echo "📜 $(service)サービスのログを表示中...";
	@docker-compose logs -f $(service);
endif

exec: ## サービスコンテナに入る
ifdef service
	@docker-compose exec $(service) sh
endif

clean: ## 未使用のイメージとコンテナをクリーンアップ
ifndef service
	@docker compose down --volumes --remove-orphans
	@make -C backend clean
	@make -C frontend clean
	@docker system prune -f
else
	@make -C $(service) clean
endif

restart: ## サービスを再起動
ifndef service
	@echo "🔄 すべてのサービスを再起動中..."
	@docker-compose restart;
else
	@echo "🔄 $(service)サービスを再起動中..."
	@docker-compose restart $(service)
endif
