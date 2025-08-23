<?php

namespace App\Guards;

use Illuminate\Contracts\Auth\Guard;
use Illuminate\Auth\GuardHelpers;

use App\Utils\JWTUtil;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Support\Facades\Log;

/**
 * JWTGuard 用于鉴权和反序列化用户，这是一个全局对象
 */
class JWTGuard implements Guard
{
    use GuardHelpers;

    protected $userProvider;

    public function __construct($userProvider)
    {
        $this->userProvider = $userProvider;
    }

    public function user()
    {
        $payload = $this->parseToken();
        $is_tenant = $payload['tenant'] ?? false;
        if ($is_tenant) {
            return $this->userProvider->retrieveById($payload['id']);
        } else {
            return $this->userProvider->retrieveById($payload['id']);
        }
    }

    protected function getTokenForRequest()
    {
        $header = request()->header('Authorization');
        if ($header && strpos($header, 'Bearer ') === 0) {
            return substr($header, 7);
        }
        throw new AuthenticationException('No token provided in request header');
    }

    protected function parseToken()
    {
        $token = $this->getTokenForRequest();

        try {
            return JWTUtil::decodeToken($token);
        } catch (\Exception $e) {
            Log::error('JWTGuard error: ' . $e->getMessage());
            throw new AuthenticationException('Token validation failed: ' . $e->getMessage());
        }
    }

    public function validate(array $credentials = [])
    {
        // This method is not typically used in JWT authentication.
        return false;
    }

    public function check()
    {
        return !is_null($this->getTokenForRequest());
    }

    public function guest()
    {
        return !$this->check();
    }

    public function id()
    {
        return $this->user() ? $this->user()->id : null;
    }
}
