# CMS Project Makefile
# ======================

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
ifndef service
	@echo "🔄 构建所有服务的镜像..."
	@make -C backend build
	@make -C frontend build
else
	@echo "🔄 重启 $(service) 服务..."
	@make -C $(service) build
endif

up: ## 启动服务
	@echo "🚀 启动$(ENV)环境..."
	ENVIRONMENT=$(ENV) docker-compose up -d --timestamps  --wait

down: ## 停止服务
	@echo "🛑 停止服务..."
	@docker-compose down

ps: ## 查看服务状态
	@docker-compose ps

logs: ## 查看所有服务日志
ifndef service
	@docker-compose logs -f;
else
	echo "📜 查看$(service)服务日志...";
	@docker-compose logs -f $(service);
endif

exec: ## 进入服务容器
ifdef service
	@docker-compose exec $(service) sh
endif

clean: ## 清理未使用的镜像和容器
ifndef service
	@echo "🧹 清理未使用的镜像和容器..."
	@docker rmi cms-frontend:latest
	@docker rmi cms-backend:latest
	@docker system prune -f
else
	@echo "🧹 清理未使用的镜像和容器..."
	@docker rmi cms-$(service):latest
	@docker system prune -f
endif

restart: ## 重启服务
ifndef service
	@echo "🔄 重启所有服务..."
	@docker-compose restart;
else
	@echo "🔄 重启$(service)服务..."
	@docker-compose restart $(service)
endif
