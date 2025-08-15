<?php

namespace App\Http\Controllers\Central;

use Illuminate\Http\Request;
use App\Models\Tenant;
use App\Models\Domain;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Cache;

class TenantController
{
    public function index(Request $request)
    {
        $perPage = $request->get('per_page', 15);
        $search = $request->get('search');

        $query = Tenant::on('central')->with('domains')->whereNull('deleted_at');
        if ($search) {
            $query->where(function ($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                    ->orWhere('email', 'like', "%{$search}%");
            });
        }
        $tenants = $query->paginate($perPage);
        $tenants->getCollection()->transform(function ($tenant) {
            return [
                'id' => $tenant->id,
                'name' => $tenant->name ?? '未设置',
                'email' => $tenant->email ?? '未设置',
                'template_key' => $tenant->template_key ?? 'default',
                'domain' => $tenant->domains->first()?->domain ?? '',
                'domains' => $tenant->domains->pluck('domain')->toArray(),
                'is_active' => true,
                'created_at' => $tenant->created_at?->format('Y-m-d H:i:s'),
                'updated_at' => $tenant->updated_at?->format('Y-m-d H:i:s'),
            ];
        });

        return response()->json(['success' => true, 'data' => $tenants]);
    }

    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'domain' => [
                'required',
                'string',
                'max:255',
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
            $tenant = Tenant::create([
                'name' => $request->name,
                'email' => $request->email,
                'template_key' => $request->input('template_key', 'default'),
            ]);
            $domain = $tenant->domains()->create(['domain' => $request->domain]);

            try {
                $token = Str::random(60);
                $url = "http://" . $domain->domain . "/tenant/register?token={$token}";
                $mailText = "【Oli-CMS】管理者登録のご案内\n\n" .
                    "貴社のOli-CMSテナントが正常に作成されました。\n" .
                    "下記のリンクより管理者アカウントの登録を完了してください。\n\n" .
                    "登録用リンク: {$url}\n\n" .
                    "※本リンクは1時間後に無効となりますので、お早めにご登録ください。\n" .
                    "ご不明点がございましたら、サポートまでご連絡ください。\n\n" .
                    "Oli-CMS運営事務局";
                Mail::raw(
                    $mailText,
                    function ($message) use ($request) {
                        $message->to($request->email)->subject('【Oli-CMS】管理者登録のご案内');
                    }
                );
                Cache::store('redis')->put("tenant_register_token:{$tenant->id}:{$request->email}", $token, 3600);
            } catch (\Throwable $e) {
                Log::error('メール送信失敗', ['exception' => $e]);
            }
            return response()->json([
                'success' => true,
                'message' => '店铺创建成功，店长账号已创建并发送邮件',
                'data' => [
                    'id' => $tenant->id,
                    'name' => $tenant->name,
                    'email' => $tenant->email,
                    'template_key' => $tenant->template_key ?? 'default',
                    'domains' => [$domain->domain],
                ]
            ], 201);
        } catch (\Throwable $e) {
            Log::error('创建店铺失败', ['exception' => $e]);
            return response()->json([
                'success' => false,
                'message' => '创建失败: ' . $e->getMessage(),
            ], 500);
        }
    }

    public function show($id)
    {
        $tenant = Tenant::with('domains')->whereNull('deleted_at')->find($id);
        if (!$tenant) {
            return response()->json(['success' => false, 'message' => '店铺不存在或已删除'], 404);
        }
        return response()->json([
            'success' => true,
            'data' => [
                'id' => $tenant->id,
                'name' => $tenant->name ?? '未设置',
                'email' => $tenant->email ?? '未设置',
                'template_key' => $tenant->template_key ?? 'default',
                'domains' => $tenant->domains->pluck('domain')->toArray(),
                'created_at' => $tenant->created_at?->format('Y-m-d H:i:s'),
                'updated_at' => $tenant->updated_at?->format('Y-m-d H:i:s'),
            ]
        ]);
    }

    public function showByDomain(Request $request)
    {
        $domainStr = strtolower(trim($request->query('domain', '')));

        if ($domainStr === '') {
            return response()->json([
                'success' => false,
                'message' => '域名不能为空'
            ], 422);
        }
        $cacheKey = "domain:{$domainStr}";

        try {
            if ($cached = Cache::get($cacheKey)) {
                return response()->json($cached['payload'], $cached['status']);
            }

            $domainModel = Domain::where('domain', $domainStr)->first();

            if (!$domainModel) {
                $payload = [
                    'success' => false
                ];
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $tenant = Tenant::with('domains')
                ->where('id', $domainModel->tenant_id)
                ->whereNull('deleted_at')
                ->first();

            if (!$tenant) {
                $payload = [
                    'success' => false
                ];
                Cache::put($cacheKey, ['payload' => $payload, 'status' => 404], 300);
                return response()->json($payload, 404);
            }

            $payload = [
                'success' => true,
                'tenant_id' => $tenant->id,
                'tenant_name' => $tenant->name,
                'template_key' => $tenant->template_key,
            ];

            Cache::put($cacheKey, ['payload' => $payload, 'status' => 200], 3600);

            return response()->json($payload);
        } catch (\Throwable $e) {
            Log::error('租户校验失败', ['exception' => $e]);
            return response()->json([
                'success' => false,
                'message' => '内部错误',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    public function update(Request $request, $id)
    {
        $tenant = Tenant::find($id);
        if (!$tenant) {
            return response()->json(['success' => false, 'message' => '店铺不存在'], 404);
        }
        $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|max:255',
            'template_key' => 'nullable|string|max:64',
        ]);
        try {
            $tenant->name = $request->name;
            $tenant->email = $request->email;
            $tenant->template_key = $request->input('template_key', 'default');
            $tenant->save();
            return response()->json([
                'success' => true,
                'message' => "店铺 '{$request->name}' 更新成功！",
                'data' => [
                    'id' => $tenant->id,
                    'name' => $tenant->name,
                    'email' => $tenant->email,
                    'template_key' => $tenant->template_key ?? 'default',
                    'updated_at' => $tenant->updated_at?->format('Y-m-d H:i:s'),
                ]
            ]);
        } catch (\Exception $e) {
            Log::error('更新店铺失败', ['exception' => $e]);
            return response()->json(['success' => false, 'message' => '更新店铺失败：' . $e->getMessage()], 500);
        }
    }

    public function destroy($id)
    {
        $tenant = Tenant::find($id);
        if (!$tenant) {
            return response()->json(['success' => false, 'message' => '店铺不存在'], 404);
        }
        try {
            $domains = $tenant->domains()->pluck('domain')->all();
            $tenantName = $tenant->name ?? $tenant->id;
            $tenant->delete();
            foreach ($domains as $d) {
                Cache::forget('tenant_domain:' . $d);
            }
            return response()->json(['success' => true, 'message' => "店铺 {$tenantName} 已删除（已软删除）"]);
        } catch (\Throwable $e) {
            Log::error('删除店铺失败', ['exception' => $e]);
            return response()->json(['success' => false, 'message' => '删除失败: ' . $e->getMessage()], 500);
        }
    }

    public function stats()
    {
        $total = Tenant::on('central')->whereNull('deleted_at')->count();
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
