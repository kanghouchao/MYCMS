<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Shop;
use App\Models\ShopDomain as Domain;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\Cache;

class ShopController extends Controller
{
    /** 获取店铺列表 */
    public function index(Request $request)
    {
        $perPage = $request->get('per_page', 15);
        $search = $request->get('search');

        $query = Shop::on('central')->with('domains');
        if ($search) {
            $query->where('data->name', 'like', "%{$search}%")
                  ->orWhere('data->email', 'like', "%{$search}%");
        }
        $shops = $query->paginate($perPage);
        $shops->getCollection()->transform(function ($shop) {
            return [
                'id' => $shop->id,
                'name' => $shop->name ?? '未设置',
                'email' => $shop->email ?? '未设置',
                'template_key' => $shop->template_key ?? 'default',
                'domain' => $shop->domains->first()?->domain ?? '',
                'domains' => $shop->domains->pluck('domain')->toArray(),
                'is_active' => true,
                'created_at' => $shop->created_at?->format('Y-m-d H:i:s'),
                'updated_at' => $shop->updated_at?->format('Y-m-d H:i:s'),
            ];
        });

            return response()->json(['success' => true, 'data' => $shops]);
    }

    /** 创建店铺 */
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
            'template_key' => 'nullable|string|max:64',
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
            $shop->put('template_key', $request->input('template_key', 'default'));
            $shop->put('created_at', now());
            $shop->save();
            $domain = $shop->domains()->create(['domain' => $request->domain]);
            Cache::forget('shop_domain:'.$request->domain);
            return response()->json([
                'success' => true,
                'message' => '店铺创建成功',
                'data' => [
                    'id' => $shop->id,
                    'name' => $shop->name,
                    'email' => $shop->email,
                    'template_key' => $shop->template_key ?? 'default',
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

    /** 获取店铺详情 */
    public function show($id)
    {
        $shop = Shop::with('domains')->find($id);
        if (!$shop) {
            return response()->json(['success' => false, 'message' => '店铺不存在'], 404);
        }
        return response()->json([
            'success' => true,
            'data' => [
                'id' => $shop->id,
                'name' => $shop->name ?? '未设置',
                'email' => $shop->email ?? '未设置',
                'template_key' => $shop->template_key ?? 'default',
                'domains' => $shop->domains->map(function ($domain) {
                    return [
                        'id' => $domain->id,
                        'domain' => $domain->domain,
                        'created_at' => $domain->created_at?->format('Y-m-d H:i:s'),
                    ];
                }),
                'created_at' => $shop->created_at?->format('Y-m-d H:i:s'),
                'updated_at' => $shop->updated_at?->format('Y-m-d H:i:s'),
            ]
        ]);
    }

    /** 更新店铺 */
    public function update(Request $request, $id)
    {
        $shop = Shop::find($id);
        if (!$shop) {
            return response()->json(['success' => false, 'message' => '店铺不存在'], 404);
        }
        $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|max:255',
            'template_key' => 'nullable|string|max:64',
        ]);
        try {
            $shop->put('name', $request->name);
            $shop->put('email', $request->email);
            $shop->put('template_key', $request->input('template_key', 'default'));
            $shop->put('updated_at', now());
            $shop->save();
            return response()->json([
                'success' => true,
                'message' => "店铺 '{$request->name}' 更新成功！",
                'data' => [
                    'id' => $shop->id,
                    'name' => $shop->name,
                    'email' => $shop->email,
                    'template_key' => $shop->template_key ?? 'default',
                    'updated_at' => $shop->updated_at?->format('Y-m-d H:i:s'),
                ]
            ]);
        } catch (\Exception $e) {
            return response()->json(['success' => false, 'message' => '更新店铺失败：' . $e->getMessage()], 500);
        }
    }

    /** 删除店铺 */
    public function destroy($id)
    {
        $shop = Shop::find($id);
        if (!$shop) {
            return response()->json(['success' => false, 'message' => '店铺不存在'], 404);
        }
        try {
            $domains = $shop->domains()->pluck('domain')->all();
            $shopName = $shop->name ?? $shop->id;
            $shop->delete();
            foreach ($domains as $d) { Cache::forget('shop_domain:'.$d); }
            return response()->json(['success' => true, 'message' => "店铺 {$shopName} 已删除"]);
        } catch (\Throwable $e) {
            return response()->json(['success' => false, 'message' => '删除失败: '.$e->getMessage()], 500);
        }
    }

    /** 获取统计数据 */
    public function stats()
    {
        $total = Shop::on('central')->count();
        return response()->json([
            'success' => true,
            'data' => [
                'total' => $total,
                'active' => $total,
                'inactive' => 0,
                'pending' => 0,
            ]
        ]);
    }
}
