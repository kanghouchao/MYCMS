# Oli CMS Project Makefile
# ======================

# 默认目标
.DEFAULT_GOAL := build

# ======================
# 帮助信息
# ======================
help: ## 显示帮助信息
	@echo "Oli CMS 项目管理命令:"
	@echo ""
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""

.PHONY: help up down restart ps backend frontend build logs exec

# ======================
# 服务管理
# ======================
build: ## 构建所有服务
	@if [ -z "$(service)" ]; then \
		make -C backend build; \
		make -C frontend build; \
	fi
	@echo "🔨 构建中..."
	@make -C $(service) build

up: ## 启动所有服务
	@echo "🚀 启动服务..."
	@docker-compose up -d

down: ## 停止所有服务
	@echo "🛑 停止服务..."
	@docker-compose down

restart: ## 重启所有服务
	@echo "🔄 重启服务..."
	@docker-compose restart

ps: ## 查看服务状态
	@docker-compose ps

logs: ## 查看所有服务日志
	@if [ -z "$(service)" ]; then \
		echo "Usage: make logs service=<service>"; \
		exit 1; \
	fi
	@docker-compose logs -f $(service)

exec: ## 进入服务容器
	@if [ -z "$(service)" ]; then \
		echo "Usage: make exec service=<service>"; \
		exit 1; \
	fi
	@docker-compose exec $(service) sh