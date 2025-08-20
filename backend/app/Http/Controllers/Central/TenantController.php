<?php

namespace App\Http\Controllers\Central;

use Illuminate\Http\Request;
use App\Services\Tenant\TenantService;

class TenantController
{
    protected $tenantService;

    public function __construct(TenantService $tenantService)
    {
        $this->tenantService = $tenantService;
    }

    public function index(Request $request)
    {
        $request->validate([
            'search' => 'nullable|string|max:64',
            'per_page' => 'nullable|integer|min:1',
        ], [
            'search.max' => '搜索内容不能超过64个字符',
            'per_page.min' => '每页数量必须大于0',
        ]);
        return $this->tenantService->getAllTenants($request->get('search'), $request->get('per_page'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'domain' => [
                'required',
                'string',
                'max:255',
                'unique:domains,domain',
                function ($attribute, $value, $fail) {
                    if (!preg_match('/^[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$/', $value)) {
                        $fail('域名格式不正确');
                    }
                    if (str_starts_with($value, 'api.') || $value === 'api') {
                        $fail('域名不能使用保留值');
                    }
                }
            ],
            'email' => [
                'required',
                'email',
                'max:255',
                'unique:tenants,email,NULL,deleted_at',
                function ($attribute, $value, $fail) {
                    if (!preg_match('/^[\w\.-]+@[\w\.-]+\.\w+$/', $value)) {
                        $fail('邮箱格式不正确');
                    }
                }
            ],
            'template_key' => 'required|string',
        ], [
            'name.required' => '店铺名称不能为空',
            'name.max' => '店铺名称不能超过255个字符',
            'domain.required' => '域名不能为空',
            'domain.unique' => '域名已被占用，请更换其他域名',
            'email.required' => '邮箱不能为空',
            'email.max' => '邮箱不能超过255个字符',
            'email.unique' => '邮箱已被占用，请更换其他邮箱',
            'template_key.required' => '模板不能为空',
        ]);

        $this->tenantService->createTenant([
            'name' => $request->name,
            'domain' => $request->domain,
            'email' => $request->email,
            'template_key' => $request->template_key,
        ]);
        return response()->noContent(201);
    }

    public function show(Request $request)
    {
        $request->validate([
            'id' => 'required|string|exists:tenants,id',
        ]);
        return $this->tenantService->getTenant($request->id);
    }

    public function showByDomain(Request $request)
    {
        $request->validate([
            'domain' => 'required|string',
        ], [
            'domain.required' => '域名不能为空',
        ]);

        return $this->tenantService->getTenantByDomain($request->domain);
    }

    public function update(Request $request)
    {
        $request->validate([
            'id' => 'required|string|exists:tenants,id,deleted_at,NULL',
            'template_key' => 'required|string',
        ], [
            'id.required' => '店铺ID不能为空',
            'id.exists' => '该店铺不存在',
            'template_key.required' => '模板键名不能为空',
        ]);
        $this->tenantService->updateTenant($request->id, $request->template_key);
        return response()->noContent(204);
    }

    public function destroy(Request $request)
    {
        $request->validate([
            'id' => 'required|string|exists:tenants,id,deleted_at,NULL',
        ], [
            'id.required' => '店铺ID不能为空',
            'id.exists' => '该店铺不存在',
        ]);
        $this->tenantService->deleteTenant($request->id);
        return response()->noContent(204);
    }

    public function stats()
    {
        return $this->tenantService->count();
    }
}
