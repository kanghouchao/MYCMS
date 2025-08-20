<?php

namespace App\Http\Guards;

use Illuminate\Contracts\Auth\Guard;
use Illuminate\Auth\GuardHelpers;
use App\Models\Tenant\User as TenantUser;
use App\Models\Central\User as CentralUser;

use App\Utils\JWTUtil;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Support\Facades\Log;

class JwtGuard implements Guard
{
    use GuardHelpers;

    public function user()
    {
        $payload = $this->parseToken();
        $is_tenant = $payload['tenant'] ?? false;
        if ($is_tenant) {
            return new TenantUser([
                'id' => $payload['id'],
                'name' => $payload['name'],
                'email' => $payload['email'],
            ]);
        } else {
            return new CentralUser([
                'id' => $payload['id'],
                'name' => $payload['name'],
                'email' => $payload['email'],
            ]);
        }
    }

    protected function getTokenForRequest()
    {
        $header = request()->header('Authorization');
        if ($header && strpos($header, 'Bearer ') === 0) {
            return substr($header, 7);
        }
        return null;
    }

    protected function parseToken()
    {
        $token = $this->getTokenForRequest();
        if (!$token) {
            throw new AuthenticationException('No token provided');
        }

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
        return !is_null($this->user());
    }

    public function guest()
    {
        return !$this->check();
    }

    public function id()
    {
        return $this->user() ? $this->user()->getAuthIdentifier() : null;
    }
}
