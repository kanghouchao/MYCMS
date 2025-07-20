
# Makefile for oli-CMS
# Provides common commands for managing the development environment.

# Use bash as the shell
SHELL := /bin/bash

# Define colors for output
GREEN := \033[0;32m
YELLOW := \033[0;33m
NC := \033[0m

.PHONY: help up down ps logs composer-install npm-install migrate tinker shell-backend shell-frontend lint-backend lint-frontend

help:
	@echo -e "${YELLOW}Available commands:${NC}"
	@echo "  ${GREEN}up${NC}                - Start all services in detached mode."
	@echo "  ${GREEN}down${NC}              - Stop and remove all services."
	@echo "  ${GREEN}ps${NC}                - List running services."
	@echo "  ${GREEN}logs${NC}              - Follow logs for all services. Usage: make logs service=backend"
	@echo ""
	@echo "  ${GREEN}composer-install${NC} - Run composer install in the backend service."
	@echo "  ${GREEN}npm-install${NC}      - Run npm install in the frontend service."
	@echo ""
	@echo "  ${GREEN}migrate${NC}           - Run Laravel database migrations."
	@echo "  ${GREEN}tinker${NC}            - Start a Laravel Tinker session."
	@echo "  ${GREEN}artisan${NC}           - Run a Laravel Artisan command. Usage: make artisan command=list"
	@echo ""
	@echo "  ${GREEN}shell-backend${NC}    - Get a shell inside the backend (php) container."
	@echo "  ${GREEN}shell-frontend${NC}   - Get a shell inside the frontend (node) container."
	@echo ""
	@echo "  ${GREEN}lint-backend${NC}     - Run Laravel Pint to format backend code."
	@echo "  ${GREEN}lint-frontend${NC}    - Run ESLint for the frontend code."

# Environment Management
up:
	@echo "Starting all services..."
	docker-compose up -d --build


down:
	@echo "Stopping all services..."
	docker-compose down

ps:
	docker-compose ps

logs:
	@echo "Following logs..."
	docker-compose logs -f $(service)

# Dependency Management
composer-install:
	@echo "Installing backend dependencies..."
	docker-compose exec backend composer install

npm-install:
	@echo "Installing frontend dependencies..."
	docker-compose exec frontend npm install

# Backend Commands
migrate:
	@echo "Running database migrations..."
	docker-compose exec backend php artisan migrate

tinker:
	@echo "Starting Tinker..."
	docker-compose exec backend php artisan tinker

artisan:
	@echo "Running Artisan command: $(command)"
	docker-compose exec backend php artisan $(command)

# Shell Access
shell-backend:
	@echo "Entering backend container..."
	docker-compose exec backend bash

shell-frontend:
	@echo "Entering frontend container..."
	docker-compose exec frontend sh

# Code Styling & Linting
lint-backend:
	@echo "Running Laravel Pint..."
	docker-compose exec backend ./vendor/bin/pint

lint-frontend:
	@echo "Running ESLint..."
	docker-compose exec frontend npm run lint

