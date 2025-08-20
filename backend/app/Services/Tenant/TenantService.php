<?php

namespace App\Services\Tenant;

use App\Exceptions\ServiceException;
use App\Models\Central\Tenant;
use App\Models\Central\Domain;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Cache;

class TenantService
{

    public function createTenant(array $tenant)
    {
        DB::transaction(function () use ($tenant) {
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
            $tenant->template_key = $data['template_key'];
            $tenant->update($data);
        });
    }

    public function deleteTenant(string $id)
    {
        DB::transaction(function () use ($id) {
            $tenant = Tenant::find($id);
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
            throw new ServiceException('店铺不存在', 404);
        }
        return [
            'id' => $tenant->id,
            'name' => $tenant->name,
            'email' => $tenant->email,
            'template' => $tenant->template,
            'domains' => $tenant->domains->pluck('domain')->toArray()
        ];
    }

    public function getTenantByDomain(string $domain)
    {
        $cacheKey = "domain:{$domain}";

        if ($cached = Cache::get($cacheKey)) {
            return $cached;
        }

        $domainModel = Domain::where('domain', $domain)->first();

        if (!$domainModel) {
            $payload = [
                'success' => false
            ];
            Cache::put($cacheKey, $payload, 300);
            throw new ServiceException('域名不存在', 404);
        }

        $tenant = Tenant::with('domains')
            ->where('id', $domainModel->tenant_id)
            ->whereNull('deleted_at')
            ->first();

        if (!$tenant) {
            $payload = [
                'success' => false
            ];
            Cache::put($cacheKey, $payload, 300);
            throw new ServiceException('店铺不存在', 404);
        }

        $payload = [
            'success' => true,
            'tenant_id' => $tenant->id,
            'tenant_name' => $tenant->name,
            'template_key' => $tenant->template_key,
        ];

        Cache::put($cacheKey, $payload, 3600);

        return $payload;
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
        return $tenants->getCollection()->transform(function ($tenant) {
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
    }

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
