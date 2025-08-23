# backend/Dockerfile

# =========================================================
# 第1段階：システム依存関係と拡張機能付きのPHPベース
# =========================================================
FROM phpswoole/swoole:php8.4-alpine AS base

## システム依存関係とPHP拡張機能のインストール
RUN apk add --no-cache postgresql-dev sqlite sqlite-dev \
    && docker-php-ext-install pgsql pdo_pgsql pdo_sqlite pcntl \
    && rm -rf /var/cache/apk/*

#+=========================================================
# 第2段階：Composer依存関係のインストール
# =========================================================
FROM composer:2.8.10 AS composer_deps
WORKDIR /home/www-data

## composerファイルのコピー
COPY composer.json ./

## 依存関係のインストールと最適化オートローダーの生成
RUN composer install \
    --no-dev \
    --no-scripts \
    --ignore-platform-reqs \
    && composer dump-autoload --optimize --no-scripts \
    && composer clear-cache
#+=========================================================
# 第3段階：テスト
# =========================================================
FROM base AS test

## 作業ディレクトリの設定
WORKDIR /var/www/html

## まずアプリケーションファイルをコピー
COPY --chown=www-data:www-data . .

## composer段階からvendor依存関係をコピー
COPY --from=composer_deps --chown=www-data:www-data /home/www-data/vendor ./vendor

## テストの実行
CMD ["vendor/bin/phpunit", "--log-junit", "/var/www/html/phpunit-report.xml", "--testdox"]

#+=========================================================
# 第4段階：本番用イメージ
# =========================================================
FROM base AS production

## アプリケーション用作業ディレクトリの設定
WORKDIR /var/www/html

## まずアプリケーションファイルをコピー
COPY --chown=www-data:www-data . .

## composer段階からvendor依存関係をコピー
COPY --from=composer_deps --chown=www-data:www-data /home/www-data/vendor ./vendor

## start.shスクリプトに実行権限を付与
RUN chmod +x /var/www/html/bin/start.sh

## 非rootユーザーに切り替え
USER www-data

## Swooleサーバー用ポート公開
EXPOSE 8000

## ヘルスチェック
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8000/up || exit 1

## アプリケーション起動
CMD ["sh", "/var/www/html/bin/start.sh"]
