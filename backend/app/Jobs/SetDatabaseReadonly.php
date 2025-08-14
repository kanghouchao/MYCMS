<?php

namespace App\Jobs;

use Stancl\Tenancy\Events\TenantDeleted;
use Illuminate\Support\Facades\DB;

class SetDatabaseReadonly
{
    /**
     * Handle the event.
     * @param TenantDeleted $event
     */
    public function handle(TenantDeleted $event)
    {
    $tenant = $event->tenant;
    // PostgreSQL 设置数据库为只读模式
    $dbName = $tenant->database()->getName();
    // 需用超级用户连接 central 数据库执行
    DB::connection('central')->statement("ALTER DATABASE \"{$dbName}\" SET default_transaction_read_only = true;");
    }
}
