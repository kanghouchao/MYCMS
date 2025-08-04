#!/bin/bash

# 确保当前用户有权限修改文件
if [ "$(id -u)" = "0" ]; then
    # 如果是root用户，设置正确的所有者
    chown -R www-data:www-data /var/www/html/storage 2>/dev/null || true
    chown -R www-data:www-data /var/www/html/bootstrap/cache 2>/dev/null || true
    chmod -R 775 /var/www/html/storage 2>/dev/null || true
    chmod -R 775 /var/www/html/bootstrap/cache 2>/dev/null || true
else
    # 如果不是root用户，尝试创建目录并设置权限
    mkdir -p /var/www/html/storage/logs 2>/dev/null || true
    mkdir -p /var/www/html/bootstrap/cache 2>/dev/null || true
    chmod -R 775 /var/www/html/storage 2>/dev/null || true
    chmod -R 775 /var/www/html/bootstrap/cache 2>/dev/null || true
fi

# 清除缓存
php artisan config:clear
php artisan route:clear
php artisan view:clear

# 生成密钥（如果需要）
if [ -z "$(grep 'APP_KEY=' .env | cut -d'=' -f2)" ]; then
    php artisan key:generate --force
fi

# 等待数据库连接
echo "等待数据库连接..."
max_attempts=30
attempt=0
until pg_isready -h oli-cms-database -p 5432 -U oli_user -d oli_cms >/dev/null 2>&1; do
    attempt=$((attempt + 1))
    if [ $attempt -gt $max_attempts ]; then
        echo "数据库连接超时，尝试使用 PHP 连接测试..."
        break
    fi
    echo "数据库尚未就绪，等待中... ($attempt/$max_attempts)"
    sleep 2
done

# 如果 pg_isready 不可用，使用 PHP 测试连接
if ! command -v pg_isready >/dev/null 2>&1; then
    echo "使用 PHP 测试数据库连接..."
    max_attempts=30
    attempt=0
    until php -r "
        try {
            \$pdo = new PDO('pgsql:host=oli-cms-database;port=5432;dbname=oli_cms', 'oli_user', 'password');
            echo 'Database connection successful\n';
            exit(0);
        } catch (Exception \$e) {
            exit(1);
        }
    " >/dev/null 2>&1; do
        attempt=$((attempt + 1))
        if [ $attempt -gt $max_attempts ]; then
            echo "数据库连接失败，继续启动应用..."
            break
        fi
        echo "数据库连接测试失败，等待中... ($attempt/$max_attempts)"
        sleep 2
    done
fi

# 运行迁移
echo "运行数据库迁移..."
php artisan migrate --force

# 运行种子（仅在没有管理员的情况下）
if [ "$(php artisan tinker --execute='echo App\Models\Admin::count();')" = "0" ]; then
    echo "创建默认管理员账户..."
    php artisan db:seed --class=AdminSeeder --force
fi

echo "应用程序启动完成！"

# 使用自定义 Swoole HTTP 服务器
echo "启动 Swoole HTTP 服务器..."
exec php swoole-server.php
