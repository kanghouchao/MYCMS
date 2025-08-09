<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Template;

class TemplateSeeder extends Seeder
{
    public function run(): void
    {
        // 默认模板
        Template::updateOrCreate(
            ['key' => 'default'],
            [
                'name' => '默认模板',
                'version' => '1.0.0',
                'enabled' => true,
                'config' => [
                    'description' => '系统内置默认模板',
                ],
            ]
        );
    }
}
