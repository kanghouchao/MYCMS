<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Log;
use Symfony\Component\HttpFoundation\Response;

class DynamicCors
{
    /**
     * Handle an incoming request.
     */
    public function handle(Request $request, Closure $next): Response
    {
        // 处理预检请求
        if ($request->getMethod() === "OPTIONS") {
            return $this->handlePreflightRequest($request);
        }

        $response = $next($request);

        // 添加 CORS 头
        return $this->addCorsHeaders($request, $response);
    }

    /**
     * 处理预检请求
     */
    private function handlePreflightRequest(Request $request): Response
    {
        $response = response('', 200);
        return $this->addCorsHeaders($request, $response);
    }

    /**
     * 添加 CORS 头
     */
    private function addCorsHeaders(Request $request, Response $response): Response
    {
        $origin = $request->headers->get('Origin');

        // 对于租户验证端点，允许所有域名访问
        if ($this->isTenantValidationEndpoint($request)) {
            $response->headers->set('Access-Control-Allow-Origin', $origin ?: '*');
            $response->headers->set('Access-Control-Allow-Credentials', 'false'); // 验证端点不需要凭证
        } elseif ($this->isOriginAllowed($origin)) {
            $response->headers->set('Access-Control-Allow-Origin', $origin);
            $response->headers->set('Access-Control-Allow-Credentials', 'true');
        }

        $response->headers->set('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS, PATCH');
        $response->headers->set('Access-Control-Allow-Headers', 'Origin, Content-Type, Accept, Authorization, X-Requested-With');
        $response->headers->set('Access-Control-Expose-Headers', '');
        $response->headers->set('Access-Control-Max-Age', '86400'); // 24小时

        return $response;
    }

    /**
     * 检查是否是租户验证端点
     */
    private function isTenantValidationEndpoint(Request $request): bool
    {
        $path = $request->getPathInfo();

        // 允许租户验证相关的端点被所有域名访问
        $publicEndpoints = [
            '/api/tenant/validate',
            '/api/tenant/check',
            '/api/tenant/info',
        ];

        return in_array($path, $publicEndpoints);
    }    /**
     * 检查来源是否被允许
     */
    private function isOriginAllowed(?string $origin): bool
    {
        if (!$origin) {
            return false;
        }

        // 静态允许的域名
        $staticAllowedOrigins = [
            'http://oli-cms.test'
        ];

        if (in_array($origin, $staticAllowedOrigins)) {
            return true;
        }

        // 检查域名模式
        $allowedPatterns = [
            '/^https?:\/\/.*\.oli-cms\.test$/'
        ];

        foreach ($allowedPatterns as $pattern) {
            if (preg_match($pattern, $origin)) {
                return true;
            }
        }

        // 动态检查租户域名
        return $this->isTenantDomain($origin);
    }

    /**
     * 检查是否是租户域名
     */
    private function isTenantDomain(string $origin): bool
    {
        try {
            // 提取域名（去掉协议和端口）
            $parsedUrl = parse_url($origin);
            if (!$parsedUrl || !isset($parsedUrl['host'])) {
                return false;
            }

            $domain = $parsedUrl['host'];

            // 使用缓存来避免频繁数据库查询
            $cacheKey = 'tenant_domain_' . $domain;

            return Cache::remember($cacheKey, 300, function () use ($domain) { // 缓存5分钟
                // 查询数据库检查是否是租户域名
                // 使用 central 连接查询租户域名
                return DB::connection('central')
                    ->table('domains')
                    ->where('domain', $domain)
                    ->exists();
            });
        } catch (\Exception $e) {
            // 如果数据库查询失败，记录错误但不阻塞请求
            Log::warning('CORS tenant domain check failed: ' . $e->getMessage());
            return false;
        }
    }
}
