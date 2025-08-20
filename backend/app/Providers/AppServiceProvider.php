<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use Illuminate\Support\Facades\Log;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register()
    {
        $this->app->singleton('jwt.secret', function () {
            $key = config('jwt.secret', env('JWT_SECRET'));
            return str_starts_with($key, 'base64:') ? base64_decode(substr($key, 7)) : $key;
        });
    }
}
