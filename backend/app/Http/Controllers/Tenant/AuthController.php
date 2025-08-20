<?php

namespace App\Http\Controllers\Tenant;

use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Redis;
use App\Models\Tenant\User;
use App\Utils\JWTUtil;
use Illuminate\Auth\AuthenticationException;

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

        $redisKey = "tenant_register_token:{$tenantId}";
        $savedToken = Redis::get($redisKey);

        if (!$savedToken || $savedToken !== $token) {
            throw new AuthenticationException('Invalid or expired registration token', ['tenant'], 401);
        }

        User::create([
            'name' => 'Admin',
            'email' => $email,
            'password' => bcrypt($password),
            'role' => 'admin',
        ]);

        Redis::del($redisKey);

        return response()->json(201);
    }

    public function login(Request $request): JsonResponse
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required|string',
        ]);
        $email = $request->input('email');
        $password = $request->input('password');

        $user = User::where('email', $email)->first();
        if (!$user || !password_verify($password, $user->password)) {
            throw new AuthenticationException('Invalid credentials', ['tenant'], 401);
        }

        $payload = [
            'id' => $user->id,
            'email' => $user->email,
            'name' => $user->name,
            'is_tenant' => true,
        ];

        return response()->json(JWTUtil::generateToken($payload));
    }

    public function logout(Request $request): JsonResponse
    {
        return response()->json();
    }
}
