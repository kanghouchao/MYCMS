<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\Admin\AuthController;
use App\Http\Controllers\Api\Admin\TenantController as AdminTenantController;
use App\Http\Controllers\Api\TenantValidationController;
use App\Http\Controllers\HealthController;

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

// 健康检查（统一控制器实现）
Route::get('health', [HealthController::class, 'check']);

// 语义化：解析租户域名 -> 返回租户信息或 404
Route::get('tenants/{domain}', [TenantValidationController::class, 'show'])
    ->where('domain', '.+');

// 管理员 API 路由
Route::prefix('admin')->name('api.admin.')->group(function () {
    // 公开路由（不需要认证）
    Route::post('login', [AuthController::class, 'login'])->name('login');

    // 需要认证的路由
    Route::middleware(['stateless_auth'])->group(function () {
        // 认证相关
        Route::get('me', [AuthController::class, 'me'])->name('me');
        Route::post('logout', [AuthController::class, 'logout'])->name('logout');

    // 租户 CRUD + 统计（统一控制器）
    Route::get('tenants', [AdminTenantController::class, 'index'])->name('tenants.index');
    Route::get('tenants/stats', [AdminTenantController::class, 'stats'])->name('tenants.stats');
    Route::post('tenants', [AdminTenantController::class, 'store'])->name('tenants.store');
    Route::get('tenants/{id}', [AdminTenantController::class, 'show'])->name('tenants.show');
    Route::put('tenants/{id}', [AdminTenantController::class, 'update'])->name('tenants.update');
    Route::delete('tenants/{id}', [AdminTenantController::class, 'destroy'])->name('tenants.destroy');
    });
});
