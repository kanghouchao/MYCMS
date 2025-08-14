<?php

namespace App\Http\Controllers;

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

        if (!$tenant || !empty($tenant->deleted_at)) {
            return response()->json([
                'error' => 'No tenant found for domain: ' . $currentDomain,
                'message' => '该域名未配置租户信息或租户已被删除'
            ], 404);
        }

        return response()->json([
            'id' => $tenant->id,
            'name' => $tenant->name ?? '未命名租户',
            'description' => $tenant->description ?? '',
            'template_key' => $tenant->template_key ?? 'default',
            'status' => $tenant->status ?? 'active',
            'domain' => $currentDomain,
            'created_at' => $tenant->created_at,
        ]);
    }
}
