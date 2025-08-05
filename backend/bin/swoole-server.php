<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Swoole\Http\Server;
use Swoole\Http\Request;
use Swoole\Http\Response;

// 启动应用
$app = require_once __DIR__ . '/../bootstrap/app.php';

// 创建 Swoole HTTP 服务器
$server = new Server('0.0.0.0', 8000);

// 配置服务器
$server->set([
    'worker_num' => 1,
    'daemonize' => false,
    'log_file' => __DIR__ . '/../storage/logs/swoole.log',
    'log_level' => SWOOLE_LOG_INFO,
]);

// 处理 HTTP 请求
$server->on('request', function (Request $request, Response $response) use ($app) {
    try {
        // 设置 CORS 头
        $response->header('Access-Control-Allow-Origin', 'http://oli-cms.test');
        $response->header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
        $response->header('Access-Control-Allow-Headers', 'Origin, Content-Type, Cookie, X-CSRF-TOKEN, Accept, Authorization, X-XSRF-TOKEN, X-Requested-With');
        $response->header('Access-Control-Allow-Credentials', 'true');

        // 处理 OPTIONS 预检请求
        if ($request->server['request_method'] === 'OPTIONS') {
            $response->status(200);
            $response->end();
            return;
        }

        // 构建 Laravel 请求
        $laravelRequest = \Illuminate\Http\Request::create(
            $request->server['request_uri'] ?? '/',
            $request->server['request_method'] ?? 'GET',
            $request->post ?? [],
            $request->cookie ?? [],
            $request->files ?? [],
            array_change_key_case($request->server ?? [], CASE_UPPER),
            $request->rawContent()
        );

        // 设置请求头
        if (isset($request->header)) {
            foreach ($request->header as $key => $value) {
                $laravelRequest->headers->set($key, $value);
            }
        }

        // 处理请求
        $laravelResponse = $app->handle($laravelRequest);

        // 设置响应状态码
        $response->status($laravelResponse->getStatusCode());

        // 设置响应头
        foreach ($laravelResponse->headers->all() as $name => $values) {
            foreach ($values as $value) {
                $response->header($name, $value);
            }
        }

        // 发送响应内容
        $response->end($laravelResponse->getContent());

    } catch (\Throwable $e) {
        error_log("Swoole server error: " . $e->getMessage() . "\n" . $e->getTraceAsString());

        $response->status(500);
        $response->header('Content-Type', 'application/json');
        $response->end(json_encode([
            'error' => 'Internal Server Error',
            'message' => $e->getMessage()
        ]));
    }
});

echo "Starting Swoole HTTP Server on http://0.0.0.0:8000\n";
$server->start();
