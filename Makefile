
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

test: ## バックエンドのテストを実行
ifdef service
	@echo "🔍 $(service)サービスのテストを実行中..."
	@make -C $(service) test
else
	@echo "🔍 すべてのサービスのテストを実行中..."
	@make -C backend test
	@make -C frontend test
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
