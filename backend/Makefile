# Backend Docker Makefile
# ======================

# 变量定义
DOCKER_IMAGE = cms-backend
DOCKER_TAG = latest

# 默认目标
.DEFAULT_GOAL := help

# ======================
# 帮助信息
# ======================
help: ## 显示帮助信息
	@echo "可用命令:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# ======================
# Docker 镜像管理
# ======================
build: ## 构建Docker镜像
	@echo "🏗️  构建Docker镜像..."
	@docker build -t $(DOCKER_IMAGE):$(DOCKER_TAG) .

rmi: ## 删除Docker镜像
	@echo "🗑️  删除Docker镜像..."
	@docker rmi $(DOCKER_IMAGE):$(DOCKER_TAG) 2>/dev/null || echo "镜像不存在或已删除"

.PHONY: help build rmi
