<?php

namespace App\Models\Central;

use Illuminate\Database\Eloquent\Model;
use Stancl\Tenancy\Contracts;
use Stancl\Tenancy\Database\Concerns\CentralConnection;
use Stancl\Tenancy\Database\Concerns\EnsuresDomainIsNotOccupied;
use Stancl\Tenancy\Database\Concerns\ConvertsDomainsToLowercase;
use Stancl\Tenancy\Database\Concerns\InvalidatesTenantsResolverCache;
use Stancl\Tenancy\Events;

/**
 * 店铺域名模型
 */
class Domain extends Model implements Contracts\Domain
{
    use CentralConnection,
        EnsuresDomainIsNotOccupied,
        ConvertsDomainsToLowercase,
        InvalidatesTenantsResolverCache;

    protected $guarded = [];
    protected $table = 'domains';

    public function tenant()
    {
        return $this->belongsTo(config('tenancy.tenant_model'), 'tenant_id');
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
