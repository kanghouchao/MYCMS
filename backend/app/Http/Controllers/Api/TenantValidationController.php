<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Log;

/**
 * 提供NextJS的Middleware
 * GET /api/tenant/validate
 */
class TenantValidationController extends Controller
{

    public function show(Request $request)
    {
        $domain = strtolower(trim($request->query('domain', '')));

        if ($domain === '') {
            return response()->json([
                'success' => false,
                'message' => '域名不能为空'
            ], 422);
        }
        $cacheKey = "shop_domain:{$domain}";

        try {
            if ($cached = Cache::get($cacheKey)) {
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
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $shop = DB::connection('central')
                ->table('shops')
                ->where('id', $shopDomain->shop_id)
                ->first();

            $templateKey = $shop->template_key ?? 'default';
            $shopName = $shop->name ?? 'Unknown';
            $payload = [
                'success' => true,
                'domain' => $domain,
                'shop_id' => $shop->id,
                'shop_name' => $shopName,
                'template_key' => $templateKey,
            ];

            Cache::put($cacheKey, ['payload' => $payload, 'status' => 200], 3600);

            return response()->json($payload);
        } catch (\Throwable $e) {
            Log::error('租户校验失败', ['exception' => $e]);
            return response()->json([
                'success' => false,
                'message' => '内部错误',
                'error' => $e->getMessage(),
            ], 500);
        }
    }
}
