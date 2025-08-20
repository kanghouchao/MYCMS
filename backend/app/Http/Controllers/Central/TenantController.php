<?php

namespace App\Http\Controllers\Central;

use Illuminate\Http\Request;
use App\Models\Central\Tenant;
use App\Models\Central\Domain;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Cache;
use App\Services\TenantService;

class TenantController
{
    protected $tenantService;

    public function __construct(TenantService $tenantService)
    {
        $this->tenantService = $tenantService;
    }

    public function index(Request $request)
    {
        $request->validate([
            'search' => 'nullable|string|max:64',
            'per_page' => 'nullable|integer|min:1',
        ]);
        return $this->tenantService->getAllTenants($request->get('search'), $request->get('per_page'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'domain' => [
                'required',
                'string',
                'max:255',
                function ($attribute, $value, $fail) {
                    if (!preg_match('/^[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$/', $value)) {
                        $fail('域名格式不正确');
                    }
                    if (str_starts_with($value, 'api.') || $value === 'api') {
                        $fail('不能使用保留域名');
                    }
                }
            ],
            'email' => [
                'required',
                'email',
                'max:255',
                function ($attribute, $value, $fail) {
                    if (!preg_match('/^[\w\.-]+@[\w\.-]+\.\w+$/', $value)) {
                        $fail('邮箱格式不正确');
                    }
                }
            ],
            'template_key' => 'required|string|max:128',
        ]);

        $this->tenantService->createTenant([
            'name' => $request->name,
            'domain' => $request->domain,
            'email' => $request->email,
            'template_key' => $request->template_key,
        ]);
        return response()->noContent(201);
    }

    public function show($id)
    {
        return $this->tenantService->getTenant($id);
    }

    public function showByDomain(Request $request)
    {
        $domainStr = strtolower(trim($request->query('domain', '')));

        if ($domainStr === '') {
            return response()->json([
                'success' => false,
                'message' => '域名不能为空'
            ], 422);
        }
        $cacheKey = "domain:{$domainStr}";

        try {
            if ($cached = Cache::get($cacheKey)) {
                return response()->json($cached['payload'], $cached['status']);
            }

            $domainModel = Domain::where('domain', $domainStr)->first();

            if (!$domainModel) {
                $payload = [
                    'success' => false
                ];
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $tenant = Tenant::with('domains')
                ->where('id', $domainModel->tenant_id)
                ->whereNull('deleted_at')
                ->first();

            if (!$tenant) {
                $payload = [
                    'success' => false
                ];
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $payload = [
                'success' => true,
                'tenant_id' => $tenant->id,
                'tenant_name' => $tenant->name,
                'template_key' => $tenant->template_key,
            ];

            Cache::put($cacheKey, ['payload' => $payload, 'status' => 200], 3600);

            return response()->json($payload);
        } catch (\Throwable $e) {
            Log::error('租户校验失败', ['exception' => $e]);
            return response()->json([
                'success' => false,
                'message' => '内部错误',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    public function update(Request $request, $id)
    {
        $tenant = Tenant::find($id);
        $request->validate([
            'template_key' => 'nullable|string|max:64',
        ]);
        $this->tenantService->updateTenant($id, $request->only(['template_key']));
        return response()->noContent(204);
    }

    public function destroy($id)
    {
        $this->tenantService->deleteTenant($id);
        return response()->noContent(204);
    }

    public function stats()
    {
        return response()->json([
            'success' => true,
            'data' => $this->tenantService->count()
        ]);
    }
}
