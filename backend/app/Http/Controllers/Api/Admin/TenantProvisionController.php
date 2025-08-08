<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Str;
use App\Models\ShopDomain as Domain;
use App\Models\Shop;

class TenantProvisionController extends Controller
{
    /**
     * 创建店铺
     */
    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'domain' => [
                'required','string','max:255',
                function ($attribute, $value, $fail) {
                    if (!preg_match('/^[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$/', $value)) {
                        $fail('域名格式不正确');
                    }
                    if (str_starts_with($value, 'api.') || $value === 'api') {
                        $fail('不能使用保留域名');
                    }
                }
            ],
            'email' => 'required|email|max:255',
            'plan' => 'required|in:basic,premium,enterprise',
        ]);

        if (Domain::on('central')->where('domain', $request->domain)->exists()) {
            return response()->json([
                'success' => false,
                'errors' => ['domain' => ['该域名已被使用']]
            ], 422);
        }

        try {
            $shop = Shop::create(['id' => Str::random(8)]);
            $shop->put('name', $request->name);
            $shop->put('email', $request->email);
            $shop->put('plan', $request->plan);
            $shop->put('created_at', now());
            $shop->save();

            $domain = $shop->domains()->create(['domain' => $request->domain]);

            Cache::forget('tenant_domain:'.$request->domain);

            return response()->json([
                'success' => true,
                'message' => '店铺创建成功',
                'data' => [
                    'id' => $shop->id,
                    'name' => $shop->name,
                    'email' => $shop->email,
                    'plan' => $shop->plan,
                    'domains' => [$domain->domain],
                ]
            ], 201);
        } catch (\Throwable $e) {
            return response()->json([
                'success' => false,
                'message' => '创建失败: '.$e->getMessage(),
            ], 500);
        }
    }

    /**
     * 删除店铺
     */
    public function destroy(string $id)
    {
        $shop = Shop::find($id);
        if (!$shop) {
            return response()->json([
                'success' => false,
                'message' => '店铺不存在'
            ], 404);
        }

        try {
            $domains = $shop->domains()->pluck('domain')->all();
            $shopName = $shop->name ?? $shop->id;
            $shop->delete();
            foreach ($domains as $d) {
                Cache::forget('tenant_domain:'.$d);
            }
            return response()->json([
                'success' => true,
                'message' => "店铺 {$shopName} 已删除"
            ]);
        } catch (\Throwable $e) {
            return response()->json([
                'success' => false,
                'message' => '删除失败: '.$e->getMessage(),
            ], 500);
        }
    }
}
