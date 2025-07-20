
# backend/Dockerfile
# Dockerfile for the Laravel backend service (API only).

# Use official PHP 8.3 FPM image as a base
FROM php:8.3-fpm-alpine

# Set working directory
WORKDIR /var/www/html

# Install system dependencies required by Laravel
# - postgresql-dev: for pdo_pgsql extension
# - libzip-dev: for zip extension
# - oniguruma-dev: for mbstring extension
RUN apk add --no-cache \
    nginx \
    supervisor \
    curl \
    libzip-dev \
    zip \
    unzip \
    postgresql-dev

# Install PHP extensions
RUN docker-php-ext-install pdo pdo_pgsql zip

# Get latest Composer
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

# Copy existing application directory contents
COPY . /var/www/html

# Copy Nginx and Supervisor configurations
# These will be created in a later step
# COPY docker/nginx.conf /etc/nginx/nginx.conf
# COPY docker/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Expose port 80 and start php-fpm and nginx
EXPOSE 80

# We will use a startup script later to run migrations and start services
# CMD ["/var/www/html/docker/start.sh"]

# For now, just start the services
CMD ["/bin/sh", "-c", "php-fpm & nginx -g 'daemon off;'"]

