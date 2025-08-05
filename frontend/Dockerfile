
# frontend/Dockerfile
# Two-stage build for Next.js frontend with Nginx

# =========================================================
# Stage 1: Build the application
# =========================================================
FROM node:24.5.0-alpine3.22 AS builder
WORKDIR /app

# Copy package.json and install dependencies
COPY package.json ./
RUN npm install

# Copy source code and build
COPY . .
RUN npm run build

# Verify that the build output exists
RUN ls -la /app/out || (echo "Build output directory not found!" && exit 1)

# =========================================================
# Stage 2: Production runtime with Nginx
# =========================================================
FROM nginx:stable-alpine3.21 AS production

# Copy built static files from builder stage
COPY --from=builder /app/out /usr/share/nginx/html

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Install curl for health check
RUN apk add --no-cache curl

# Set correct permissions for nginx user (already exists in nginx:alpine)
RUN chown -R nginx:nginx /usr/share/nginx/html \
    && chown -R nginx:nginx /var/cache/nginx \
    && chown -R nginx:nginx /var/log/nginx \
    && chown -R nginx:nginx /etc/nginx/conf.d \
    && touch /var/run/nginx.pid \
    && chown -R nginx:nginx /var/run/nginx.pid

# Switch to non-root user
USER nginx

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
