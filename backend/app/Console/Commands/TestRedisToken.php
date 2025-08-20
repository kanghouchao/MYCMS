<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Illuminate\Support\Facades\Cache;

class TestRedisToken extends Command
{
    protected $signature = 'test:redis-token {tenantId}';
    protected $description = 'テナント登録トークンのRedis値を確認する';

    public function handle()
    {
        $tenantId = $this->argument('tenantId');
        $redisKey = "tenant_register_token:{$tenantId}";
        $value = Cache::get($redisKey);

        $this->info("Redis key: {$redisKey}");
        $this->info("Value: " . var_export($value, true));
    }
}
