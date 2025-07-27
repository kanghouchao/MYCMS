
# Makefile for oli-CMS
# Provides common commands for managing the development environment.

# Use bash as the shell for advanced features
SHELL := /bin/bash

# Define colors for output
GREEN  := \033[0;32m
YELLOW := \033[0;33m
NC     := \033[0m

# Default to showing the help message if no target is provided
.DEFAULT_GOAL := help

# All targets are .PHONY to prevent conflicts with file names
.PHONY: help up down build restart ps logs
.PHONY: artisan composer tinker migrate fresh-db clear-cache lint-backend shell-backend
.PHONY: npm lint-frontend shell-frontend

# This is a catch-all for commands that take arguments.
# It filters out the target name from the command line arguments.
%:
	@:

help: ## ✨ 显示此帮助信息
	@echo -e "${YELLOW}用法:${NC} make [target] [arguments]${NC}"
	@echo ""
	@echo -e "${YELLOW}可用命令:${NC}"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  ${GREEN}%-20s${NC} %s\n", $$1, $$2}'

# ==============================================================================
# Docker 环境管理
# ==============================================================================

up: ## 🚀 以后台模式启动所有服务
	@echo -e "${GREEN}正在启动所有服务...${NC}"
	@docker-compose up -d

down: ## 🛑 停止并移除所有服务、网络和卷
	@echo -e "${GREEN}正在停止所有服务...${NC}"
	@docker-compose down

build: ## 🛠️  构建或重新构建服务 (例如: make build backend)
	@echo -e "${GREEN}正在构建服务: $(or $(filter-out $@,$(MAKECMDGOALS)),all)...${NC}"
	@docker-compose build $(filter-out $@,$(MAKECMDGOALS))

restart: ## 🔄 重启服务 (例如: make restart backend)
	@echo -e "${GREEN}正在重启服务: $(or $(filter-out $@,$(MAKECMDGOALS)),all)...${NC}"
	@docker-compose restart $(filter-out $@,$(MAKECMDGOALS))

ps: ## 📊 列出正在运行的服务
	@docker-compose ps

logs: ## 📜 查看服务日志 (例如: make logs backend)
	@echo -e "${GREEN}正在查看日志: $(or $(filter-out $@,$(MAKECMDGOALS)),all services)...${NC}"
	@docker-compose logs -f $(filter-out $@,$(MAKECMDGOALS))

# ==============================================================================
# 后端 (Laravel) 命令
# ==============================================================================

artisan: ## 🐘 运行 Laravel Artisan 命令 (例如: make artisan list)
	@echo -e "${GREEN}正在运行: php artisan $(filter-out $@,$(MAKECMDGOALS))...${NC}"
	@docker-compose exec backend php artisan $(filter-out $@,$(MAKECMDGOALS))

composer: ## 📦 运行 Composer 命令 (例如: make composer install)
	@echo -e "${GREEN}正在运行: composer $(filter-out $@,$(MAKECMDGOALS))...${NC}"
	@docker-compose exec backend composer $(filter-out $@,$(MAKECMDGOALS))

tinker: ## 💡 启动 Laravel Tinker 会话
	@make artisan tinker

migrate: ## 🗄️  运行数据库迁移
	@make artisan migrate

fresh-db: ## ✨ 重建数据库并填充数据
	@make artisan migrate:fresh --seed

clear-cache: ## 🧹 清除所有 Laravel 缓存
	@make artisan cache:clear
	@make artisan route:clear
	@make artisan config:clear
	@make artisan view:clear

lint-backend: ## 💅 运行 Laravel Pint 格式化后端代码
	@echo -e "${GREEN}正在运行 Laravel Pint...${NC}"
	@docker-compose exec backend ./vendor/bin/pint

shell-backend: ## 💻 进入后端容器的 shell 环境
	@echo -e "${GREEN}正在进入后端容器 (sh)...${NC}"
	@docker-compose exec backend sh

# ==============================================================================
# 前端 (Next.js) 命令
# ==============================================================================

npm: ## 📦 运行 npm 命令 (例如: make npm install)
	@echo -e "${GREEN}正在运行: npm $(filter-out $@,$(MAKECMDGOALS))...${NC}"
	@docker-compose exec frontend npm $(filter-out $@,$(MAKECMDGOALS))

lint-frontend: ## 💅 运行 ESLint 检查前端代码
	@make npm run lint

shell-frontend: ## 💻 进入前端容器的 shell 环境
	@echo -e "${GREEN}正在进入前端容器...${NC}"
	@docker-compose exec frontend sh

