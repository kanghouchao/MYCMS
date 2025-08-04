<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Stancl\Tenancy\Database\Models\Tenant;
use Stancl\Tenancy\Database\Models\Domain;
use Illuminate\Support\Str;
use Illuminate\Validation\Rule;

class TenantController extends Controller
{
    /**
     * 显示租户列表
     */
    public function index()
    {
        $tenants = Tenant::with('domains')->paginate(10);
        return view('admin.tenants.index', compact('tenants'));
    }

    /**
     * 显示创建租户页面
     */
    public function create()
    {
        return view('admin.tenants.create');
    }

    /**
     * 存储新租户
     */
    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'domain' => 'required|string|max:255|unique:domains,domain',
            'email' => 'required|email|max:255',
            'plan' => 'required|in:basic,premium,enterprise',
        ]);

        // 创建租户
        $tenant = Tenant::create([
            'id' => Str::random(8),
        ]);

        // 设置租户数据
        $tenant->put('name', $request->name);
        $tenant->put('email', $request->email);
        $tenant->put('plan', $request->plan);
        $tenant->put('created_at', now());
        $tenant->save();

        // 创建域名
        $domain = $tenant->domains()->create([
            'domain' => $request->domain,
        ]);

        return redirect()->route('admin.tenants.index')
            ->with('success', "租户 '{$request->name}' 创建成功！域名：{$request->domain}");
    }

    /**
     * 显示租户详情
     */
    public function show(Tenant $tenant)
    {
        $tenant->load('domains');
        return view('admin.tenants.show', compact('tenant'));
    }

    /**
     * 显示编辑租户页面
     */
    public function edit(Tenant $tenant)
    {
        return view('admin.tenants.edit', compact('tenant'));
    }

    /**
     * 更新租户
     */
    public function update(Request $request, Tenant $tenant)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|max:255',
            'plan' => 'required|in:basic,premium,enterprise',
        ]);

        $tenant->put('name', $request->name);
        $tenant->put('email', $request->email);
        $tenant->put('plan', $request->plan);
        $tenant->put('updated_at', now());
        $tenant->save();

        return redirect()->route('admin.tenants.index')
            ->with('success', "租户 '{$request->name}' 更新成功！");
    }

    /**
     * 删除租户
     */
    public function destroy(Tenant $tenant)
    {
        $tenantName = $tenant->name;
        $tenant->delete();

        return redirect()->route('admin.tenants.index')
            ->with('success', "租户 '{$tenantName}' 删除成功！");
    }
}
