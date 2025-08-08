<?php

namespace App\Models;

use Stancl\Tenancy\Database\Models\Tenant as BaseTenant;
use Stancl\Tenancy\Contracts\TenantWithDatabase;
use Stancl\Tenancy\Database\Concerns\HasDatabase;
use Stancl\Tenancy\Database\Concerns\HasDomains;

class Tenant extends BaseTenant implements TenantWithDatabase
{
    use HasDatabase, HasDomains;

    /**
     * The attributes that are fillable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'id',
        'data',
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'data' => 'array',
    ];

    /**
     * 获取租户数据属性
     *
     * @param string $key
     * @return mixed
     */
    public function __get($key)
    {
        // 先从 data JSON 字段中获取数据
        if (isset($this->data[$key])) {
            return $this->data[$key];
        }

        // 然后从模型属性中获取
        return parent::__get($key);
    }

    /**
     * 设置租户数据属性
     *
     * @param string $key
     * @param mixed $value
     * @return void
     */
    public function put(string $key, $value): void
    {
        $data = $this->data ?? [];
        $data[$key] = $value;
        $this->data = $data;
    }

    /**
     * 检查租户是否有指定的域名
     *
     * @param string $domain
     * @return bool
     */
    public function hasDomain(string $domain): bool
    {
        return $this->domains()->where('domain', $domain)->exists();
    }

    /**
     * 获取租户的主域名
     *
     * @return string|null
     */
    public function getPrimaryDomain(): ?string
    {
        return $this->domains()->first()?->domain;
    }
}
