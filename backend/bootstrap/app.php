<?php

use App\Exceptions\ServiceException;
use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        api: __DIR__ . '/../routes/api.php',
        commands: __DIR__ . '/../routes/console.php',
        health: '/up',
        then: function () {
            Route::middleware(['tenant', 'api'])
                ->prefix('api')
                ->group(base_path('routes/tenant.php'));
        },
    )
    ->withMiddleware(function (Middleware $middleware): void {

        $middleware->group('tenant', [
            Stancl\Tenancy\Middleware\InitializeTenancyByDomain::class,
            Stancl\Tenancy\Middleware\PreventAccessFromCentralDomains::class,
        ]);

        //TODO should delete
        $middleware->api(prepend: [
            \App\Http\Middleware\DynamicCors::class,
        ]);

        $middleware->alias([
            'role'                   => \App\Http\Middleware\RoleMiddleware::class,
            'stateless_auth'         => \App\Http\Middleware\StatelessAuth::class,
        ]);
    })
    ->withExceptions(function (Exceptions $exceptions) {
        $exceptions->render(function (ServiceException $e, Request $request) {
            return $e->render($request);
        });
    })
    ->create();
