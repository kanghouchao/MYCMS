<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\RefreshDatabase;

class HealthTest extends TestCase
{
    public function test_health_endpoint_ok(): void
    {
        $resp = $this->getJson('/api/health');
        $resp->assertStatus(200)
            ->assertJsonStructure([
                'status', 'timestamp', 'services' => ['database' => ['status'], 'cache' => ['status']]
            ]);
    }
}
