# backend/Dockerfile

# =========================================================
# Stage 1: PHP with Swoole extensions
# =========================================================
FROM phpswoole/swoole:php8.4-alpine AS base
WORKDIR /var/www/html

# Install system dependencies for PHP extensions
RUN apk add --no-cache \
    libzip-dev \
    postgresql-dev \
    bash \
    curl \
    git \
    unzip

# Install PHP extensions
RUN docker-php-ext-install pdo pdo_pgsql zip pcntl

# =========================================================
# Stage 2: Install Composer dependencies
# =========================================================
FROM composer:2.8.10 AS composer_deps
WORKDIR /app
COPY composer.json ./
RUN composer install --no-dev --no-scripts --ignore-platform-reqs

# =========================================================
# Stage 3: Final Production Image
# =========================================================
FROM base AS production
WORKDIR /var/www/html

# Copy vendor from previous stage
COPY --from=composer_deps /app/vendor /var/www/html/vendor

# Copy application code
COPY . .

# Ensure .env exists for artisan commands
RUN cp .env.example .env || true

# Optimize autoloader
RUN composer dump-autoload --optimize

# Create storage/octane and set permissions
RUN mkdir -p /var/www/html/storage/octane \
    && chown -R www-data:www-data /var/www/html/storage /var/www/html/bootstrap/cache \
    && chmod -R 775 /var/www/html/storage /var/www/html/bootstrap/cache

# Switch to www-data for security
USER www-data

# Expose Octane default port
EXPOSE 8000

# Start Laravel Octane with Swoole
CMD ["php", "artisan", "octane:start", "--server=swoole", "--host=0.0.0.0", "--port=8000", "--workers=auto"]
