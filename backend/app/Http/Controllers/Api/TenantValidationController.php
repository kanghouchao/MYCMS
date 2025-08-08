<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Cache;

class TenantValidationController extends Controller
{
    /**
     * 语义化：解析域名 -> 返回租户信息 / 404
     * GET /api/tenants/{domain}
     */
    public function show(Request $request, string $domain)
    {
        $domain = trim($domain);
        if ($domain === '') {
            return response()->json([
                'success' => false,
                'message' => '域名不能为空'
            ], 422);
        }

        $cacheKey = "tenant_domain:{$domain}";

        try {
            if ($cached = Cache::get($cacheKey)) {
                // 命中缓存（存在或不存在都缓存过）
                return response()->json($cached['payload'], $cached['status']);
            }

            $tenantDomain = DB::connection('central')
                ->table('domains')
                ->where('domain', $domain)
                ->first();

            if (!$tenantDomain) {
                $payload = [
                    'success' => false,
                    'message' => '租户域名不存在',
                    'domain' => $domain,
                ];
                // 不存在缓存 5 分钟
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $tenant = DB::connection('central')
                ->table('tenants')
                ->where('id', $tenantDomain->tenant_id)
                ->first();

            $payload = [
                'success' => true,
                'domain' => $domain,
                'tenant_id' => $tenant->id,
                'tenant_name' => json_decode($tenant->data, true)['name'] ?? 'Unknown',
            ];

            // 存在缓存 1 小时
            Cache::put($cacheKey, ['payload' => $payload, 'status' => 200], 3600);

            return response()->json($payload);
        } catch (\Throwable $e) {
            return response()->json([
                'success' => false,
                'message' => '内部错误',
                'error' => $e->getMessage(),
            ], 500);
        }
    }
}
