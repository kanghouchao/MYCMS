<?php

namespace App\Http\Controllers\Api\User;

use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use App\Http\Controllers\Controller;

class AuthController extends Controller
{
    // 用户登录（租户）
    public function login(Request $request): JsonResponse
    {
        // 这里只做示例，实际应校验账号密码并生成 token
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

    // 获取当前用户信息
    public function me(Request $request): JsonResponse
    {
        // 实际应通过 token 获取用户信息
        $user = [
            'id' => 1,
            'name' => 'Tenant User',
            'email' => 'tenant@example.com',
        ];
        return response()->json(['user' => $user]);
    }

    // 用户退出登录
    public function logout(Request $request): JsonResponse
    {
        // 实际应清除 token 或做相关处理
        return response()->json(['message' => 'Logged out successfully.']);
    }
}
