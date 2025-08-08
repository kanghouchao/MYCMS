<?php

namespace App\Models;

use Stancl\Tenancy\Database\Models\Tenant as BaseTenant;
use Stancl\Tenancy\Contracts\TenantWithDatabase;
use Stancl\Tenancy\Database\Concerns\HasDatabase;
use Stancl\Tenancy\Database\Concerns\HasDomains;

/**
 * 店铺模型（原 Tenant ）
 */
class Shop extends BaseTenant implements TenantWithDatabase
{
    use HasDatabase, HasDomains;

    protected $table = 'shops';
    protected $primaryKey = 'id';

    protected $fillable = [
        'id',
        'data',
    ];

    protected $casts = [
        'data' => 'array',
    ];

    public function __get($key)
    {
        if (isset($this->data[$key])) {
            return $this->data[$key];
        }
        return parent::__get($key);
    }

    public function put(string $key, $value): void
    {
        $data = $this->data ?? [];
        $data[$key] = $value;
        $this->data = $data;
    }

    public function hasDomain(string $domain): bool
    {
        return $this->domains()->where('domain', $domain)->exists();
    }

    public function getPrimaryDomain(): ?string
    {
        return $this->domains()->first()?->domain;
    }

    // 覆盖 trait 默认的 tenant_id 外键，改为 shop_id
    public function domains()
    {
        return $this->hasMany(config('tenancy.domain_model'), 'shop_id');
    }
}
