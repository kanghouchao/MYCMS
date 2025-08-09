<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class RoleMiddleware
{
    /**
     * Ensure the authenticated admin has one of the required roles.
     */
    public function handle(Request $request, Closure $next, string ...$roles): Response
    {
        $admin = $request->user();

        if (!$admin || !($admin instanceof \App\Models\Admin)) {
            return response()->json([
                'success' => false,
                'message' => '未授权访问',
            ], 401);
        }

        if (!$admin->isActive()) {
            return response()->json([
                'success' => false,
                'message' => '账户已被停用',
            ], 403);
        }

        if (!empty($roles) && !in_array($admin->role, $roles, true)) {
            return response()->json([
                'success' => false,
                'message' => '权限不足',
            ], 403);
        }

        return $next($request);
    }
}
