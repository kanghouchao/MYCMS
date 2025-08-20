<?php

declare(strict_types=1);

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Tenant\AuthController;
use App\Http\Controllers\Tenant\UserController;

Route::prefix('user')->name('api.user.')->group(function () {
    Route::post('login', [AuthController::class, 'login']);

    Route::post('register', [AuthController::class, 'register']);

    Route::middleware('stateless_auth')->group(function () {
        Route::get('me', [UserController::class, 'me']);
        Route::post('logout', [AuthController::class, 'logout']);
    });
});
