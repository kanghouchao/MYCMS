<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\Admin\AuthController;
use App\Http\Controllers\Api\Admin\ShopController as AdminShopController;
use App\Http\Controllers\Api\Admin\TemplateController as AdminTemplateController;
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
            Route::get('shops', [AdminShopController::class, 'index'])->name('shops.index');
            Route::get('shops/stats', [AdminShopController::class, 'stats'])->name('shops.stats');
            Route::post('shops', [AdminShopController::class, 'store'])->name('shops.store');
            Route::get('shops/{id}', [AdminShopController::class, 'show'])->name('shops.show');
            Route::put('shops/{id}', [AdminShopController::class, 'update'])->name('shops.update');
            Route::delete('shops/{id}', [AdminShopController::class, 'destroy'])->name('shops.destroy');

            Route::get('templates', [AdminTemplateController::class, 'index'])->name('templates.index');
            Route::put('templates/{id}', [AdminTemplateController::class, 'update'])->name('templates.update');
        });
    });
});
