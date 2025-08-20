<?php

namespace App\Utils;

use Exception;
use Firebase\JWT\JWT;
use Firebase\JWT\Key;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Support\Facades\Log;

class JWTUtil
{

    public static function generateToken($payload)
    {
        $time = time();
        $expiresAt = $time + config('jwt.ttl', env('JWT_TTL', 3600));
        $payload['exp'] = $expiresAt;
        $payload['iat'] = $time;
        $payload['jti'] = uniqid();
        return [
            'token' => JWT::encode($payload, app('jwt.secret'), 'HS256'),
            'expires_at' => $expiresAt
        ];
    }

    public static function decodeToken($token)
    {
        try {
            return (array)JWT::decode($token, new Key(app('jwt.secret'), 'HS256'));
        } catch (Exception $e) {
            throw new AuthenticationException('Token validation failed: ' . $e->getMessage());
        }
    }
}
