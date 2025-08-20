<?php

namespace App\Utils;

use Firebase\JWT\JWT;
use Firebase\JWT\Key;

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
        return (array)JWT::decode($token, new Key(app('jwt.secret'), 'HS256'));
    }
}
