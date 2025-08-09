<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Cache;

class TenantValidationController extends Controller
{
    /**
    * 解析域名 -> 返回店铺信息 / 404
    * GET /api/shops/{domain}
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

    $cacheKey = "shop_domain:{$domain}";

        try {
            if ($cached = Cache::get($cacheKey)) {
                // 命中缓存（存在或不存在都缓存过）
                return response()->json($cached['payload'], $cached['status']);
            }

            $shopDomain = DB::connection('central')
                ->table('domains')
                ->where('domain', $domain)
                ->first();

            if (!$shopDomain) {
                $payload = [
                    'success' => false,
                    'message' => '店铺域名不存在',
                    'domain' => $domain,
                ];
                // 不存在缓存 5 分钟
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $shop = DB::connection('central')
                ->table('shops')
                ->where('id', $shopDomain->shop_id)
                ->first();

            $shopData = json_decode($shop->data, true) ?: [];
            $templateKey = $shopData['template_key'] ?? 'default';
            $payload = [
                'success' => true,
                'domain' => $domain,
                'shop_id' => $shop->id,
                'shop_name' => $shopData['name'] ?? 'Unknown',
                'template_key' => $templateKey,
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
