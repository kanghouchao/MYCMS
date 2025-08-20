<?php

namespace App\Http\Controllers\Central;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Hash;
use Illuminate\Auth\AuthenticationException;
use App\Models\Central\User;
use App\Utils\JWTUtil;

/**
 * AuthController for managing admin authentication
 * @kanghouchao
 */
class AuthController
{

    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required|string|min:6',
        ]);

        Log::debug('Admin login attempt', ['email' => $request->input('email')]);

        $user = User::where('email', $request->input('email'))->first();

        if (!$user || !Hash::check($request->input('password'), $user->password)) {
            throw new AuthenticationException('password or email is incorrect', ['central'], 401);
        }

        if (!$user->isActive()) {
            throw new AuthenticationException('this account is not active', ['central'], 401);
        }

        $payload = [
            'id' => $user->id,
            'email' => $user->email,
            'name' => $user->name,
            'role' => $user->role,
            'is_tenant' => false,
        ];

        return response()->json(JWTUtil::generateToken($payload));
    }

    public function me(Request $request)
    {
        return response()->json(224);
    }

    public function logout(Request $request)
    {
        return response()->json();
    }
}
