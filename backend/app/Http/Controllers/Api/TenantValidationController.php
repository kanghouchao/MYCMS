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
        $cacheKey = "domain:{$domain}";

        try {
            if ($cached = Cache::get($cacheKey)) {
                return response()->json($cached['payload'], $cached['status']);
            }

            $domain = DB::connection('central')
                ->table('domains')
                ->where('domain', $domain)
                ->first();

            if (!$domain) {
                $payload = [
                    'success' => false,
                    'message' => '店铺域名不存在',
                    'domain' => $domain,
                ];
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $tenant = DB::connection('central')
                ->table('tenants')
                ->where('id', $domain->tenant_id)
                ->whereNull('deleted_at')
                ->first();

            if (!$tenant) {
                $payload = [
                    'success' => false,
                    'message' => '店舗が削除されたか、存在しません',
                    'domain' => $domain,
                ];
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $templateKey = $tenant->template_key ?? 'default';
            $tenantName = $tenant->name ?? 'Unknown';
            $payload = [
                'success' => true,
                'domain' => $domain,
                'tenant_id' => $tenant->id,
                'tenant_name' => $tenantName,
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
