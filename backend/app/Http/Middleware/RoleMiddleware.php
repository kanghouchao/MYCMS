<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
use Illuminate\Support\Facades\Log;

class RoleMiddleware
{
    /**
     * Ensure the authenticated admin has one of the required roles.
     */
    public function handle(Request $request, Closure $next, string ...$roles): Response
    {
        $user = $request->user();
        Log::debug('Admin user information retrieved', ['roles' => $roles]);
        //TODO: Implement proper response handling
        return $next($request);
    }
}
