# Oli CMS Project Makefile
# ======================

# 默认环境
ENV ?= local

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
build: ## 构建镜像
	@echo "🛠️  构建$(service)服务镜像..."
	@make -C $(service) build


up: ## 启动服务
	@echo "🚀 启动$(ENV)环境..."
	ENVIRONMENT=$(ENV) docker-compose up -d --timestamps  --wait

down: ## 停止服务
	@echo "🛑 停止服务..."
	@docker-compose down

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
