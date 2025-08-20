<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Central\AuthController;
use App\Http\Controllers\Central\TenantController;
use App\Http\Controllers\Central\TemplateController;

Route::get('tenant', [TenantController::class, 'showByDomain']);

Route::prefix('admin')->name('api.admin.')->group(function () {
    Route::post('login', [AuthController::class, 'login'])->name('login');

    Route::middleware(['stateless_auth'])->group(function () {
        Route::get('me', [AuthController::class, 'me'])->name('me');
        Route::post('logout', [AuthController::class, 'logout'])->name('logout');

        Route::middleware('role:admin')->group(function () {
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
