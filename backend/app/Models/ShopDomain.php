<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Stancl\Tenancy\Contracts;
use Stancl\Tenancy\Database\Concerns\CentralConnection;
use Stancl\Tenancy\Database\Concerns\EnsuresDomainIsNotOccupied;
use Stancl\Tenancy\Database\Concerns\ConvertsDomainsToLowercase;
use Stancl\Tenancy\Database\Concerns\InvalidatesTenantsResolverCache;
use Stancl\Tenancy\Events;

/**
 * 店铺域名模型（替换包默认 Domain）
 */
class ShopDomain extends Model implements Contracts\Domain
{
    use CentralConnection,
        EnsuresDomainIsNotOccupied,
        ConvertsDomainsToLowercase,
        InvalidatesTenantsResolverCache;

    protected $guarded = [];
    protected $table = 'domains';

    public function shop()
    {
        return $this->belongsTo(config('tenancy.tenant_model'), 'shop_id');
    }

    // 兼容原调用 $domain->tenant
    public function tenant()
    {
        return $this->shop();
    }

    protected $dispatchesEvents = [
        'saving' => Events\SavingDomain::class,
        'saved' => Events\DomainSaved::class,
        'creating' => Events\CreatingDomain::class,
        'created' => Events\DomainCreated::class,
        'updating' => Events\UpdatingDomain::class,
        'updated' => Events\DomainUpdated::class,
        'deleting' => Events\DeletingDomain::class,
        'deleted' => Events\DomainDeleted::class,
    ];
}
