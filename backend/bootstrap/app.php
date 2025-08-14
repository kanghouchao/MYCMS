<?php

use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Support\Facades\Route;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        api: __DIR__.'/../routes/api.php',
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
        then: function () {
            Route::middleware([
                Stancl\Tenancy\Middleware\InitializeTenancyByDomain::class,
                Stancl\Tenancy\Middleware\PreventAccessFromCentralDomains::class,
            ])->group(base_path('routes/tenant.php'));
        },
    )
    ->withMiddleware(function (Middleware $middleware): void {
        $middleware->api(prepend: [
            \App\Http\Middleware\DynamicCors::class,
        ]);

        $middleware->alias([
            'guard' => \App\Http\Middleware\AdminGuard::class,
            'stateless_auth' => \App\Http\Middleware\StatelessAuth::class,
            'role' => \App\Http\Middleware\RoleMiddleware::class,
            'stateless_auth' => \App\Http\Middleware\StatelessAuth::class,
        ]);
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        //
    })->create();
