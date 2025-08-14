# backend/Dockerfile

# =========================================================
# Stage 1: Base PHP with system dependencies and extensions
# =========================================================
FROM phpswoole/swoole:php8.4-alpine AS base

# Install system dependencies and PHP extensions
RUN apk add --no-cache postgresql-dev sqlite sqlite-dev \
    && docker-php-ext-install pgsql pdo_pgsql pdo_sqlite pcntl \
    && rm -rf /var/cache/apk/*

# =========================================================
# Stage 2: Install Composer dependencies
# =========================================================
FROM composer:2.8.10 AS composer_deps
WORKDIR /home/www-data

# Copy composer files
# Do not copy the composer.lock file. We always install the latest dependencies
COPY composer.json ./

# Install dependencies and generate optimized autoloader
RUN composer install \
    --no-dev \
    --no-scripts \
    --ignore-platform-reqs \
    && composer dump-autoload --optimize --no-scripts \
    && composer clear-cache

# =========================================================
# Stage 3: Production image
# =========================================================
FROM base AS production

# Set working directory for application
WORKDIR /var/www/html

# Copy application files first
COPY --chown=www-data:www-data . .

# Copy vendor dependencies from composer stage
COPY --from=composer_deps --chown=www-data:www-data /home/www-data/vendor ./vendor

# Set executable permissions on start script
RUN chmod +x /var/www/html/bin/start.sh

# Switch to non-root user
USER www-data

# Expose port for Swoole server
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8000/up || exit 1

# Start the application
CMD ["sh", "/var/www/html/bin/start.sh"]
