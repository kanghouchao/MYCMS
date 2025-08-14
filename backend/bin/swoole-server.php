<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Swoole\Http\Server;
use Swoole\Http\Request;
use Swoole\Http\Response;

$app = require_once __DIR__ . '/../bootstrap/app.php';

$server = new Server('0.0.0.0', 8000);

$server->set([
    'worker_num' => 1,
    'daemonize' => false,
    'log_file' => __DIR__ . '/../storage/logs/swoole.log',
    'log_level' => SWOOLE_LOG_INFO,
]);

$server->on('request', function (Request $request, Response $response) use ($app) {
    try {

        if (($request->server['request_method'] ?? 'GET') === 'OPTIONS') {
            $response->status(200);
            $response->end();
            return;
        }

        // 组装 URI（带 query string）
        $uri = $request->server['request_uri'] ?? '/';
        $queryString = $request->server['query_string'] ?? '';
        if ($queryString !== '') {
            $uri .= '?' . $queryString;
        }

        // 方法与参数
        $method = strtoupper($request->server['request_method'] ?? 'GET');
        $getParams = $request->get ?? [];
        $postParams = $request->post ?? [];
        $parameters = $method === 'GET' ? $getParams : array_merge($getParams, $postParams);

        // 服务器变量
        $serverVars = array_change_key_case($request->server ?? [], CASE_UPPER);

        // 创建 Laravel Request
        $laravelRequest = \Illuminate\Http\Request::create(
            $uri,
            $method,
            $parameters,
            $request->cookie ?? [],
            $request->files ?? [],
            $serverVars,
            $request->rawContent()
        );

        // 设置 query/request
        if (!empty($getParams)) {
            $laravelRequest->query->replace($getParams);
        }
        if (!empty($postParams)) {
            $laravelRequest->request->replace(array_merge($laravelRequest->request->all(), $postParams));
        }

        // 设置请求头
        if (!empty($request->header)) {
            foreach ($request->header as $key => $value) {
                $laravelRequest->headers->set($key, $value);
            }
        }

        // 处理请求
        $laravelResponse = $app->handle($laravelRequest);

        // 输出响应
        $response->status($laravelResponse->getStatusCode());
        foreach ($laravelResponse->headers->all() as $name => $values) {
            foreach ($values as $value) {
                $response->header($name, $value);
            }
        }
        $response->end($laravelResponse->getContent());

    } catch (\Throwable $e) {
        error_log("Swoole server error: " . $e->getMessage());
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
