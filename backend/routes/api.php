<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\Admin\AuthController;
use App\Http\Controllers\Api\Admin\TenantController;
use App\Http\Controllers\Api\Admin\TemplateController;
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

// 获取店铺信息
Route::get('tenant', [TenantValidationController::class, 'show']);

// 管理员 API 路由
Route::prefix('admin')->name('api.admin.')->group(function () {
    Route::post('login', [AuthController::class, 'login'])->name('login');

    Route::middleware(['stateless_auth'])->group(function () {
        Route::get('me', [AuthController::class, 'me'])->name('me');
        Route::post('logout', [AuthController::class, 'logout'])->name('logout');

        Route::middleware('role:super_admin')->group(function () {
            Route::get('tenants', [TenantController::class, 'index'])->name('tenants.index');
            Route::get('tenants/stats', [TenantController::class, 'stats'])->name('tenants.stats');
            Route::post('tenants', [TenantController::class, 'store'])->name('tenants.store');
            Route::get('tenants/{id}', [TenantController::class, 'show'])->name('tenants.show');
            Route::put('tenants/{id}', [TenantController::class, 'update'])->name('tenants.update');
            Route::delete('tenants/{id}', [TenantController::class, 'destroy'])->name('tenants.destroy');

            Route::get('templates', [TemplateController::class, 'index'])->name('templates.index');
            Route::put('templates/{id}', [TemplateController::class, 'update'])->name('templates.update');
        });
    });
});
