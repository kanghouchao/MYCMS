<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Admin\AuthController;
use App\Http\Controllers\Admin\DashboardController;
use App\Http\Controllers\Admin\TenantController;

Route::get('/', function () {
    return view('welcome');
});

// 管理员路由
Route::prefix('admin')->name('admin.')->group(function () {
    // 认证路由（未登录用户可访问）
    Route::middleware('guest:admin')->group(function () {
        Route::get('login', [AuthController::class, 'showLoginForm'])->name('login');
        Route::post('login', [AuthController::class, 'login']);
    });

    // 需要认证的管理员路由
    Route::middleware(['App\Http\Middleware\AdminAuthenticate'])->group(function () {
        Route::post('logout', [AuthController::class, 'logout'])->name('logout');
        Route::get('dashboard', [DashboardController::class, 'index'])->name('dashboard');

        // 租户管理
        Route::resource('tenants', TenantController::class);
    });
});
