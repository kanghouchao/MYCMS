<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
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

            // 创建无状态 JWT-like token
            $payload = [
                'admin_id' => $admin->id,
                'email' => $admin->email,
                'exp' => time() + 3600 * 24 * 7, // 7天过期
                'iat' => time(),
                'jti' => uniqid(), // JWT ID 防止重放攻击
            ];

            $payloadBase64 = base64_encode(json_encode($payload));
            $signature = hash_hmac('sha256', $payloadBase64, config('app.key'));
            $token = $payloadBase64 . '.' . $signature;

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
                    'token' => $token,
                    'expires_at' => date('Y-m-d H:i:s', $payload['exp'])
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
        // 从请求中获取 token
        $token = $request->bearerToken();
        if (!$token) {
            return response()->json([
                'success' => false,
                'message' => '未提供认证令牌'
            ], 401);
        }

        // 验证 token
        $admin = $this->validateToken($token);
        if (!$admin) {
            return response()->json([
                'success' => false,
                'message' => '未认证或令牌无效'
            ], 401);
        }

        return response()->json([
            'success' => true,
            'data' => [
                'admin' => [
                    'id' => $admin->id,
                    'name' => $admin->name,
                    'email' => $admin->email,
                    'role' => $admin->role,
                    'created_at' => $admin->created_at,
                    'updated_at' => $admin->updated_at,
                ]
            ]
        ]);
    }

    /**
     * 管理员登出
     */
    public function logout(Request $request)
    {
        // 无状态 token，客户端直接删除即可
        return response()->json([
            'success' => true,
            'message' => '登出成功'
        ]);
    }

    /**
     * 验证无状态 token
     */
    private function validateToken($token)
    {
        try {
            // 分解 token
            $parts = explode('.', $token);
            if (count($parts) !== 2) {
                return null;
            }

            [$payloadBase64, $signature] = $parts;

            // 验证签名
            $expectedSignature = hash_hmac('sha256', $payloadBase64, config('app.key'));
            if (!hash_equals($signature, $expectedSignature)) {
                return null;
            }

            // 解析 payload
            $payload = json_decode(base64_decode($payloadBase64), true);
            if (!$payload) {
                return null;
            }

            // 检查过期时间
            if ($payload['exp'] < time()) {
                return null;
            }

            // 获取管理员并验证
            $admin = Admin::find($payload['admin_id']);
            if (!$admin || $admin->email !== $payload['email']) {
                return null;
            }

            return $admin;
        } catch (\Exception $e) {
            return null;
        }
    }
}
