<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Central\User;
use Illuminate\Support\Facades\Hash;

class AdminSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Ensure super admin exists or create it
        if (!User::where('email', env('ADMIN_EMAIL', 'admin@cms.com'))->exists()) {
            User::create([
                'name' => 'Super Admin',
                'email' => env('ADMIN_EMAIL', 'admin@cms.com'),
                'password' => Hash::make(env('ADMIN_PASSWORD', 'admin123')),
                'role' => 'super_admin',
                'is_active' => true,
            ]);
        }
    }
}
