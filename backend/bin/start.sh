#!/bin/sh

# 确保必要的目录存在
mkdir -p storage/logs bootstrap/cache

# 等待数据库连接
echo "等待数据库连接..."
until php artisan migrate:status >/dev/null 2>&1; do
    echo "数据库尚未就绪，等待中..."
    sleep 2
done

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
exec php bin/swoole-server.php
