<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class TenantController extends Controller
{
    /**
     * 获取当前租户信息
     */
    public function getCurrentTenant(): JsonResponse
    {
        $currentDomain = request()->getHost();
        $tenant = tenant();

        // 如果没有找到租户，说明这个域名没有对应的租户
        if (!$tenant) {
            return response()->json([
                'error' => 'No tenant found for domain: ' . $currentDomain,
                'message' => '该域名未配置租户信息'
            ], 404);
        }

        return response()->json([
            'id' => $tenant->id,
            'name' => $tenant->name ?? '未命名租户',
            'description' => $tenant->description ?? '',
            'plan' => $tenant->plan ?? 'basic',
            'status' => $tenant->status ?? 'active',
            'domain' => $currentDomain,
            'created_at' => $tenant->created_at,
        ]);
    }
}
