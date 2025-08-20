<?php

namespace App\Models\Central;

use Stancl\Tenancy\Database\Models\Tenant as BaseTenant;
use Illuminate\Database\Eloquent\SoftDeletes;
use Stancl\Tenancy\Contracts\TenantWithDatabase;
use Stancl\Tenancy\Database\Concerns\HasDatabase;
use Stancl\Tenancy\Database\Concerns\HasDomains;

class Tenant extends BaseTenant implements TenantWithDatabase
{

    use HasDatabase, HasDomains, SoftDeletes;

    public $incrementing = false;

    protected $keyType = 'string';

    public static function getCustomColumns(): array
    {
        return ['id', 'name', 'email', 'template_key', 'is_active', 'created_at', 'updated_at', 'deleted_at'];
    }

    protected $table = 'tenants';
    protected $primaryKey = 'id';

    protected $fillable = [
        'name',
        'email',
        'template_key',
        'data',
        'is_active',
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

    public function domains()
    {
        return $this->hasMany(config('tenancy.domain_model'), 'tenant_id');
    }

    public function template()
    {
        return $this->belongsTo(Template::class, 'template_key', 'key');
    }
}
