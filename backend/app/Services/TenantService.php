<?php

namespace App\Services;

use App\Exceptions\ServiceException;
use App\Models\Central\Tenant;
use App\Models\Central\Domain;
use App\Models\Central\Template;
use Illuminate\Support\Facades\DB;

class TenantService
{

    public function createTenant(array $tenant)
    {
        DB::transaction(function () use ($tenant) {
            if (Domain::where('domain', $tenant['domain'])->exists()) {
                throw new ServiceException('422', '域名已被占用，请更换其他域名');
            }
            if (Tenant::where('email', $tenant['email'])->whereNull('deleted_at')->exists()) {
                throw new ServiceException('422', '邮箱已被占用，请更换其他邮箱');
            }
            if (!Template::where('key', $tenant['template_key'])->where('enabled', true)->exists()) {
                throw new ServiceException('422', '模板不存在或未启用');
            }
            Tenant::create([
                'name' => $tenant['name'],
                'email' => $tenant['email'],
                'template_key' => $tenant['template_key'],
            ])->domains()->create([
                'domain' => $tenant['domain']
            ]);
        });
    }

    public function updateTenant(string $id, array $data)
    {
        DB::transaction(function () use ($id, $data) {
            $tenant = Tenant::find($id);
            if (!$tenant) {
                throw new ServiceException('404', '店铺不存在');
            }
            if (!Template::where('key', $tenant['template_key'])->where('enabled', true)->exists()) {
                throw new ServiceException('422', '模板不存在或未启用');
            }
            $tenant->update($data);
        });
    }

    public function deleteTenant(string $id)
    {
        DB::transaction(function () use ($id) {
            $tenant = Tenant::find($id);
            if (!$tenant) {
                throw new ServiceException('404', '店铺不存在');
            }
            foreach ($tenant->domains as $domain) {
                $domain->delete();
            }
            $tenant->delete();
        });
    }

    public function getTenant(string $id)
    {
        $tenant = Tenant::with(['domains', 'template'])->whereNull('deleted_at')->find($id);
        if (!$tenant) {
            return response()->json(['success' => false, 'message' => '店铺不存在或已删除'], 404);
        }
        return response()->json([
            'success' => true,
            'data' => [
                'id' => $tenant->id,
                'name' => $tenant->name,
                'email' => $tenant->email,
                'template' => $tenant->template,
                'domains' => $tenant->domains->pluck('domain')->toArray()
            ]
        ]);
    }

    public function getAllTenants($search = null, int $perPage = 15)
    {
        $query = Tenant::with('domains')->whereNull('deleted_at');
        if ($search) {
            $query->where(function ($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                    ->orWhere('email', 'like', "%{$search}%");
            });
        }
        $tenants = $query->paginate($perPage);
        $tenants->getCollection()->transform(function ($tenant) {
            return [
                'id' => $tenant->id,
                'name' => $tenant->name,
                'email' => $tenant->email,
                'template_key' => $tenant->template_key,
                'domain' => $tenant->domains->first()?->domain ?? '',
                'domains' => $tenant->domains->pluck('domain')->toArray(),
                'is_active' => $tenant->is_active,
            ];
        });
        return response()->json(['success' => true, 'data' => $tenants]);
    }

    /**
     * 返回租户状态统计
     */
    public function count()
    {
        $stats = Tenant::selectRaw('
                COUNT(*) as total,
                SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) as active,
                SUM(CASE WHEN is_active = 0 THEN 1 ELSE 0 END) as inactive
            ')
            ->whereNull('deleted_at')
            ->first();
        return [
            'total' => (int)$stats->total,
            'active' => (int)$stats->active,
            'inactive' => (int)$stats->inactive,
            'pending' => 0,
        ];
    }
}
