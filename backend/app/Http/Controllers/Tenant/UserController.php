<?php

namespace App\Http\Controllers\Tenant;

use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class UserController
{
    public function me(Request $request): JsonResponse
    {
        $tenant = tenant();
        return response()->json(224);
    }
}
