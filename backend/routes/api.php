<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\Admin\AuthController;
use App\Http\Controllers\Api\Admin\TenantController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

// 健康检查和CORS测试
Route::get('health', function () {
    return response()->json([
        'success' => true,
        'message' => 'API is working',
        'timestamp' => now()->toISOString(),
        'cors' => 'enabled'
    ]);
});

// CORS预检测试
Route::options('{any}', function () {
    return response('', 200);
})->where('any', '.*');

// 管理员 API 路由
Route::prefix('admin')->name('api.admin.')->group(function () {
    // 公开路由（不需要认证）
    Route::post('login', [AuthController::class, 'login'])->name('login');

    // 需要认证的路由
    Route::middleware(['auth:sanctum', 'guard:admin'])->group(function () {
        // 认证相关
        Route::get('me', [AuthController::class, 'me'])->name('me');
        Route::post('logout', [AuthController::class, 'logout'])->name('logout');

        // 租户管理
        Route::get('tenants/stats', [TenantController::class, 'stats'])->name('tenants.stats');
        Route::apiResource('tenants', TenantController::class);
    });
});
