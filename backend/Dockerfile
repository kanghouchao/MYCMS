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
    postgresql-client \
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

# Copy and set permissions for start script
COPY start.sh /usr/local/bin/start.sh
RUN chmod +x /usr/local/bin/start.sh

# Ensure .env exists for artisan commands
RUN cp .env.example .env || true

# Optimize autoloader
RUN composer dump-autoload --optimize

# Create storage directories and set permissions
RUN mkdir -p /var/www/html/storage/logs \
    && chown -R www-data:www-data /var/www/html/storage /var/www/html/bootstrap/cache \
    && chmod -R 775 /var/www/html/storage /var/www/html/bootstrap/cache

# Generate application key if not exists
RUN php artisan key:generate --force || true

# Clear and cache configuration
RUN php artisan config:clear \
    && php artisan config:cache \
    && php artisan route:clear

# Switch to www-data for security
USER www-data

# Expose Laravel default port
EXPOSE 8000

# Start Laravel with traditional artisan serve
CMD ["/usr/local/bin/start.sh"]
