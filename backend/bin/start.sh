#!/bin/sh

# 运行迁移
echo "运行数据库迁移..."
php artisan migrate --seed --force

# 使用自定义 Swoole HTTP 服务器
echo "启动 Swoole HTTP 服务器..."
exec php bin/swoole-server.php
