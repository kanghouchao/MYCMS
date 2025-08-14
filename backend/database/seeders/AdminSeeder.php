<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Admin;
use Illuminate\Support\Facades\Hash;

class AdminSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Ensure super admin exists or create it
        if (!Admin::where('email', env('ADMIN_EMAIL', 'admin@cms.com'))->exists()) {
            Admin::create([
                'name' => 'Super Admin',
                'email' => env('ADMIN_EMAIL', 'admin@cms.com'),
                'password' => Hash::make(env('ADMIN_PASSWORD', 'admin123')),
                'role' => 'super_admin',
                'is_active' => true,
            ]);
        }
    }
}
