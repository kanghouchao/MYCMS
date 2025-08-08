<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Tenant;
use Stancl\Tenancy\Database\Models\Domain;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\Cache;

class TenantController extends Controller
{
    /**
     * 获取租户列表
     */
    public function index(Request $request)
    {
        $perPage = $request->get('per_page', 15);
        $search = $request->get('search');

        $query = Tenant::on('central')->with('domains');

        if ($search) {
            $query->where('data->name', 'like', "%{$search}%")
                  ->orWhere('data->email', 'like', "%{$search}%");
        }

        $tenants = $query->paginate($perPage);

        // 格式化租户数据
        $tenants->getCollection()->transform(function ($tenant) {
            return [
                'id' => $tenant->id,
                'name' => $tenant->name ?? '未设置',
                'email' => $tenant->email ?? '未设置',
                'plan' => $tenant->plan ?? 'basic',
                'domain' => $tenant->domains->first()?->domain ?? '',
                'domains' => $tenant->domains->pluck('domain')->toArray(),
                'is_active' => true, // 暂时都设为活跃状态
                'created_at' => $tenant->created_at?->format('Y-m-d H:i:s'),
                'updated_at' => $tenant->updated_at?->format('Y-m-d H:i:s'),
            ];
        });

        return response()->json([
            'success' => true,
            'data' => $tenants
        ]);
    }

    /** 创建租户 */
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
     * 获取租户详情
     */
    public function show($id)
    {
        $tenant = Tenant::with('domains')->find($id);

        if (!$tenant) {
            return response()->json([
                'success' => false,
                'message' => '租户不存在'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => [
                'id' => $tenant->id,
                'name' => $tenant->name ?? '未设置',
                'email' => $tenant->email ?? '未设置',
                'plan' => $tenant->plan ?? 'basic',
                'domains' => $tenant->domains->map(function ($domain) {
                    return [
                        'id' => $domain->id,
                        'domain' => $domain->domain,
                        'created_at' => $domain->created_at?->format('Y-m-d H:i:s'),
                    ];
                }),
                'created_at' => $tenant->created_at?->format('Y-m-d H:i:s'),
                'updated_at' => $tenant->updated_at?->format('Y-m-d H:i:s'),
            ]
        ]);
    }

    /**
     * 更新租户
     */
    public function update(Request $request, $id)
    {
        $tenant = Tenant::find($id);

        if (!$tenant) {
            return response()->json([
                'success' => false,
                'message' => '租户不存在'
            ], 404);
        }

        $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|max:255',
            'plan' => 'required|in:basic,premium,enterprise',
        ]);

        try {
            $tenant->put('name', $request->name);
            $tenant->put('email', $request->email);
            $tenant->put('plan', $request->plan);
            $tenant->put('updated_at', now());
            $tenant->save();

            return response()->json([
                'success' => true,
                'message' => "租户 '{$request->name}' 更新成功！",
                'data' => [
                    'id' => $tenant->id,
                    'name' => $tenant->name,
                    'email' => $tenant->email,
                    'plan' => $tenant->plan,
                    'updated_at' => $tenant->updated_at?->format('Y-m-d H:i:s'),
                ]
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => '更新租户失败：' . $e->getMessage()
            ], 500);
        }
    }

    /**
     * 删除租户
     */
    public function destroy($id)
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
            foreach ($domains as $d) { Cache::forget('tenant_domain:'.$d); }
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

    /**
     * 获取统计数据
     */
    public function stats()
    {
        $totalTenants = Tenant::on('central')->count();
        $activeTenants = $totalTenants; // 暂时假设所有租户都是活跃的
        $inactiveTenants = 0; // 当前没有停用功能
        $pendingTenants = 0; // 当前没有待审核功能

        return response()->json([
            'success' => true,
            'data' => [
                'total' => $totalTenants,
                'active' => $activeTenants,
                'inactive' => $inactiveTenants,
                'pending' => $pendingTenants,
            ]
        ]);
    }
}
