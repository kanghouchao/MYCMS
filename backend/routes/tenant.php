<?php

declare(strict_types=1);

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\User\AuthController;

/*
|--------------------------------------------------------------------------
| Tenant Routes
|--------------------------------------------------------------------------
|
| Here you can register the tenant routes for your application.
| These routes are loaded by the TenantRouteServiceProvider.
|
| Feel free to customize them however you want. Good luck!
|
*/

// Tenant API Routes (require tenant domain)
Route::prefix('api')->group(function () {
    // 用户登录（租户）
    Route::post('user/login', [AuthController::class, 'login']);

    // 需要登录的接口
    Route::middleware('stateless_auth')->group(function () {
        Route::get('user/me', [AuthController::class, 'me']);
        Route::post('user/logout', [AuthController::class, 'logout']);
    });
});
