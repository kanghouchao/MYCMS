<?php

namespace App\Http\Controllers\Tenant;

use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Cache;

class AuthController
{
    public function register(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required|string|min:8',
            'token' => 'required|string',
        ]);

        $tenantId = tenant()->id;
        $email = $request->input('email');
        $password = $request->input('password');
        $token = $request->input('token');

        $redisKey = "tenant_register_token:{$token}";
        $savedToken = Cache::get($redisKey);
        if (!$savedToken || $savedToken !== $token) {
            return response()->json([
                'success' => false,
                'message' => 'Token无效或已过期',
            ], 422);
        }


        $tenant = \App\Models\Tenant::find($tenantId);
        if (!$tenant) {
            return response()->json([
                'success' => false,
                'message' => '租户不存在',
            ], 404);
        }

        $db = $tenant->getConnectionName();
        try {
            $userModel = new \App\Models\User();
            $userModel->setConnection($db);
            $userModel->name = 'Admin';
            $userModel->email = $email;
            $userModel->password = bcrypt($password);
            $userModel->role = 'admin';
            $userModel->save();

            \Illuminate\Support\Facades\Cache::store('redis')->forget($redisKey);

            return response()->json([
                'success' => true,
                'message' => '管理员注册成功',
            ]);
        } catch (\Throwable $e) {
            \Illuminate\Support\Facades\Log::error('管理员注册失败', ['exception' => $e]);
            return response()->json([
                'success' => false,
                'message' => '注册失败: ' . $e->getMessage(),
            ], 500);
        }
    }

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

    public function logout(Request $request): JsonResponse
    {
        return response()->json(['message' => 'Logged out successfully.']);
    }
}
