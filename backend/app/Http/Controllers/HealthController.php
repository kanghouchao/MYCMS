<?php

namespace App\Http\Controllers;

use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Cache;
use Throwable;

class HealthController extends Controller
{
    public function check(): JsonResponse
    {
        $started = microtime(true);

        $dbStatus = 'unknown';
        $dbLatency = null;
        $redisStatus = 'unknown';
        $redisLatency = null;
        $errors = [];

        // 数据库探测
        try {
            $t1 = microtime(true);
            DB::connection('central')->select('SELECT 1');
            $dbLatency = round((microtime(true) - $t1) * 1000, 2);
            $dbStatus = 'up';
        } catch (Throwable $e) {
            $dbStatus = 'down';
            $errors['database'] = $e->getMessage();
        }

        // Redis 探测（如果默认驱动是 redis 或者配置存在）
        try {
            $t2 = microtime(true);
            Cache::put('__health_ping', '1', 10);
            Cache::get('__health_ping');
            $redisLatency = round((microtime(true) - $t2) * 1000, 2);
            $redisStatus = 'up';
        } catch (Throwable $e) {
            $redisStatus = 'down';
            $errors['cache'] = $e->getMessage();
        }

        $status = ($dbStatus === 'up' && $redisStatus === 'up') ? 'ok' : 'degraded';
        if ($dbStatus === 'down' || $redisStatus === 'down') {
            if ($dbStatus === 'down' && $redisStatus === 'down') {
                $status = 'critical';
            } elseif ($dbStatus === 'down' || $redisStatus === 'down') {
                $status = 'partial';
            }
        }

        return response()->json([
            'status' => $status,
            'timestamp' => now()->toISOString(),
            'uptime_ms' => round((microtime(true) - $started) * 1000, 2),
            'version' => config('app.version', '1.0.0'),
            'services' => [
                'database' => [
                    'status' => $dbStatus,
                    'latency_ms' => $dbLatency,
                ],
                'cache' => [
                    'status' => $redisStatus,
                    'latency_ms' => $redisLatency,
                ],
            ],
            'errors' => (object) $errors,
        ], $status === 'ok' ? 200 : 503);
    }
}
