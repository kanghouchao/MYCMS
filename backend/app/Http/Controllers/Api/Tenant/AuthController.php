<?php

namespace App\Http\Controllers\Api\User;

use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class AuthController
{
    public function login(Request $request): JsonResponse
    {
        $tenant = tenant();
        if (!$tenant || !empty($tenant->deleted_at)) {
            return response()->json([
                'error' => '租户不存在或已被删除',
                'message' => '无法登录已删除租户'
            ], 403);
        }
        $user = [
            'id' => 1,
            'name' => 'Tenant User',
            'email' => 'tenant@example.com',
        ];
        $token = 'mocked-jwt-token';
        return response()->json([
            'user' => $user,
            'token' => $token,
        ]);
    }

    public function me(Request $request): JsonResponse
    {
        $tenant = tenant();
        if (!$tenant || !empty($tenant->deleted_at)) {
            return response()->json([
                'error' => '租户不存在或已被删除',
                'message' => '无法获取已删除租户的用户信息'
            ], 403);
        }
        $user = [
            'id' => 1,
            'name' => 'Tenant User',
            'email' => 'tenant@example.com',
        ];
        return response()->json(['user' => $user]);
    }


    public function logout(Request $request): JsonResponse
    {
        return response()->json(['message' => 'Logged out successfully.']);
    }
}
