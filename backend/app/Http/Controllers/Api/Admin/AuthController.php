<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Validation\ValidationException;
use App\Models\Admin;

class AuthController extends Controller
{
    /**
     * 管理员登录
     */
    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required|string|min:6',
        ]);

        $credentials = $request->only('email', 'password');

        if (Auth::guard('admin')->attempt($credentials)) {
            $admin = Auth::guard('admin')->user();

            if (!$admin->isActive()) {
                Auth::guard('admin')->logout();
                return response()->json([
                    'success' => false,
                    'message' => '您的账户已被停用。'
                ], 401);
            }

            $token = $admin->createToken('admin-token')->plainTextToken;

            return response()->json([
                'success' => true,
                'message' => '登录成功',
                'data' => [
                    'admin' => [
                        'id' => $admin->id,
                        'name' => $admin->name,
                        'email' => $admin->email,
                        'role' => $admin->role,
                    ],
                    'token' => $token
                ]
            ]);
        }

        return response()->json([
            'success' => false,
            'message' => '邮箱或密码错误'
        ], 401);
    }

    /**
     * 获取当前登录的管理员信息
     */
    public function me(Request $request)
    {
        $admin = $request->user('admin');

        return response()->json([
            'success' => true,
            'data' => [
                'id' => $admin->id,
                'name' => $admin->name,
                'email' => $admin->email,
                'role' => $admin->role,
                'is_active' => $admin->is_active,
                'created_at' => $admin->created_at,
            ]
        ]);
    }

    /**
     * 管理员登出
     */
    public function logout(Request $request)
    {
        $request->user('admin')->currentAccessToken()->delete();

        return response()->json([
            'success' => true,
            'message' => '登出成功'
        ]);
    }
}
