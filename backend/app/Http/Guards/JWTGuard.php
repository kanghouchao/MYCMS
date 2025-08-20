<?php

namespace App\Http\Guards;

use Illuminate\Contracts\Auth\Guard;
use Illuminate\Auth\GuardHelpers;
use Illuminate\Contracts\Auth\UserProvider;
use Illuminate\Http\Request;

use App\Utils\JWTUtil;
use Illuminate\Auth\AuthenticationException;

class JWTGuard implements Guard
{
    use GuardHelpers;

    protected $request;
    protected $provider;

    public function __construct(UserProvider $provider, Request $request)
    {
        $this->provider = $provider;
        $this->request = $request;
    }

    public function user()
    {
        if ($this->user !== null) {
            return $this->user;
        }

        $token = $this->getTokenForRequest();
        if (!$token) {
            throw new AuthenticationException('Token not provided');
        }

        try {
            $payload = JWTUtil::decodeToken($token);
            $is_tenant = $payload['tenant'] ?? false;
            if ($is_tenant) {
                $this->user = \App\Models\Tenant\User::where('id', $payload['id'])->first();
            } else {
                $this->user = \App\Models\Central\User::where('id', $payload['id'])->first();
            }
        } catch (\Exception $e) {
            throw new AuthenticationException('Token validation failed: ' . $e->getMessage());
        }

        return $this->user;
    }

    protected function getTokenForRequest()
    {
        $header = $this->request->header('Authorization');
        if ($header && strpos($header, 'Bearer ') === 0) {
            return substr($header, 7);
        }
        return null;
    }

    protected function parseToken($token)
    {
        return JWTUtil::decodeToken($token);
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
