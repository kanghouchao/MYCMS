<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Str;
use Stancl\Tenancy\Database\Models\Domain;
use App\Models\Tenant;

class TenantProvisionController extends Controller
{
    /**
     * 创建租户
     */
    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'domain' => [
                'required','string','max:255',
                function ($attribute, $value, $fail) {
                    if (!preg_match('/^[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$/', $value)) {
                        $fail('域名格式不正确');
                    }
                    if (str_starts_with($value, 'api.') || $value === 'api') {
                        $fail('不能使用保留域名');
                    }
                }
            ],
            'email' => 'required|email|max:255',
            'plan' => 'required|in:basic,premium,enterprise',
        ]);

        if (Domain::on('central')->where('domain', $request->domain)->exists()) {
            return response()->json([
                'success' => false,
                'errors' => ['domain' => ['该域名已被使用']]
            ], 422);
        }

        try {
            $tenant = Tenant::create(['id' => Str::random(8)]);
            $tenant->put('name', $request->name);
            $tenant->put('email', $request->email);
            $tenant->put('plan', $request->plan);
            $tenant->put('created_at', now());
            $tenant->save();

            $domain = $tenant->domains()->create(['domain' => $request->domain]);

            Cache::forget('tenant_domain:'.$request->domain);

            return response()->json([
                'success' => true,
                'message' => '租户创建成功',
                'data' => [
                    'id' => $tenant->id,
                    'name' => $tenant->name,
                    'email' => $tenant->email,
                    'plan' => $tenant->plan,
                    'domains' => [$domain->domain],
                ]
            ], 201);
        } catch (\Throwable $e) {
            return response()->json([
                'success' => false,
                'message' => '创建失败: '.$e->getMessage(),
            ], 500);
        }
    }

    /**
     * 删除租户
     */
    public function destroy(string $id)
    {
        $tenant = Tenant::find($id);
        if (!$tenant) {
            return response()->json([
                'success' => false,
                'message' => '租户不存在'
            ], 404);
        }

        try {
            $domains = $tenant->domains()->pluck('domain')->all();
            $tenantName = $tenant->name ?? $tenant->id;
            $tenant->delete();
            foreach ($domains as $d) {
                Cache::forget('tenant_domain:'.$d);
            }
            return response()->json([
                'success' => true,
                'message' => "租户 {$tenantName} 已删除"
            ]);
        } catch (\Throwable $e) {
            return response()->json([
                'success' => false,
                'message' => '删除失败: '.$e->getMessage(),
            ], 500);
        }
    }
}
