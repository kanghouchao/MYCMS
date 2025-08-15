<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use App\Models\Admin;

class StatelessAuth
{
    /**
     * Handle an incoming request.
     */
    public function handle(Request $request, Closure $next, ...$guards)
    {
        $token = $request->bearerToken();

        if (!$token) {
            return response()->json([
                'success' => false,
                'message' => '未提供认证令牌'
            ], 401);
        }

        $admin = $this->validateToken($token);

        if (!$admin) {
            return response()->json([
                'success' => false,
                'message' => '认证令牌无效或已过期'
            ], 401);
        }

        // 将认证的管理员设置到请求中
        $request->setUserResolver(function () use ($admin) {
            return $admin;
        });

        return $next($request);
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
