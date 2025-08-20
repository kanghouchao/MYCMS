<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;

use Illuminate\Auth\AuthenticationException;

class StatelessAuth
{
    /**
     * Handle an incoming request.
     */
    public function handle(Request $request, Closure $next, ...$guards)
    {
        if (empty($guards)) {
            return $next($request);
        }

        foreach ($guards as $guard) {
            if (!auth($guard)->check()) {
                throw new AuthenticationException('認証トークンが無効です');
            }
        }

        return $next($request);
    }
}
