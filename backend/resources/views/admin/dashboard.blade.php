@extends('layouts.admin')

@section('title', '仪表板')
@section('page-title', '仪表板')

@section('content')
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
    <!-- 统计卡片 -->
    <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <i class="fas fa-users text-2xl text-indigo-600"></i>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="text-sm font-medium text-gray-500 truncate">
                            总租户数
                        </dt>
                        <dd class="text-lg font-medium text-gray-900">
                            {{ \Stancl\Tenancy\Database\Models\Tenant::count() }}
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <i class="fas fa-globe text-2xl text-green-600"></i>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="text-sm font-medium text-gray-500 truncate">
                            活跃域名
                        </dt>
                        <dd class="text-lg font-medium text-gray-900">
                            {{ \Stancl\Tenancy\Database\Models\Domain::count() }}
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <i class="fas fa-calendar-plus text-2xl text-yellow-600"></i>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="text-sm font-medium text-gray-500 truncate">
                            本月新增
                        </dt>
                        <dd class="text-lg font-medium text-gray-900">
                            {{ \Stancl\Tenancy\Database\Models\Tenant::whereMonth('created_at', now()->month)->count() }}
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <i class="fas fa-user-shield text-2xl text-purple-600"></i>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="text-sm font-medium text-gray-500 truncate">
                            管理员数
                        </dt>
                        <dd class="text-lg font-medium text-gray-900">
                            {{ \App\Models\Admin::where('is_active', true)->count() }}
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 快速操作 -->
<div class="bg-white shadow rounded-lg">
    <div class="px-4 py-5 sm:p-6">
        <h3 class="text-lg leading-6 font-medium text-gray-900 mb-4">
            <i class="fas fa-bolt mr-2"></i>快速操作
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <a href="{{ route('admin.tenants.create') }}"
               class="bg-indigo-50 hover:bg-indigo-100 border border-indigo-200 rounded-lg p-4 text-center transition duration-150 ease-in-out">
                <i class="fas fa-plus-circle text-2xl text-indigo-600 mb-2"></i>
                <p class="text-sm font-medium text-indigo-600">创建新租户</p>
            </a>

            <a href="{{ route('admin.tenants.index') }}"
               class="bg-green-50 hover:bg-green-100 border border-green-200 rounded-lg p-4 text-center transition duration-150 ease-in-out">
                <i class="fas fa-list text-2xl text-green-600 mb-2"></i>
                <p class="text-sm font-medium text-green-600">查看所有租户</p>
            </a>

            <div class="bg-gray-50 border border-gray-200 rounded-lg p-4 text-center">
                <i class="fas fa-cog text-2xl text-gray-400 mb-2"></i>
                <p class="text-sm font-medium text-gray-400">系统设置</p>
                <p class="text-xs text-gray-400 mt-1">即将推出</p>
            </div>
        </div>
    </div>
</div>

<!-- 最近的租户 -->
<div class="mt-8 bg-white shadow rounded-lg">
    <div class="px-4 py-5 sm:p-6">
        <h3 class="text-lg leading-6 font-medium text-gray-900 mb-4">
            <i class="fas fa-clock mr-2"></i>最近创建的租户
        </h3>

        @php
            $recentTenants = \Stancl\Tenancy\Database\Models\Tenant::with('domains')
                ->orderBy('created_at', 'desc')
                ->limit(5)
                ->get();
        @endphp

        @if($recentTenants->count() > 0)
            <div class="overflow-hidden">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                租户名称
                            </th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                域名
                            </th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                套餐
                            </th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                创建时间
                            </th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        @foreach($recentTenants as $tenant)
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                    {{ $tenant->name ?? '未设置' }}
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {{ $tenant->domains->first()->domain ?? '无域名' }}
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    <span class="px-2 py-1 text-xs font-medium rounded-full
                                        @if($tenant->plan === 'enterprise') bg-purple-100 text-purple-800
                                        @elseif($tenant->plan === 'premium') bg-blue-100 text-blue-800
                                        @else bg-gray-100 text-gray-800
                                        @endif">
                                        {{ ucfirst($tenant->plan ?? 'basic') }}
                                    </span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {{ $tenant->created_at ? $tenant->created_at->format('Y-m-d H:i') : '未知' }}
                                </td>
                            </tr>
                        @endforeach
                    </tbody>
                </table>
            </div>
        @else
            <div class="text-center py-8">
                <i class="fas fa-inbox text-4xl text-gray-300 mb-4"></i>
                <p class="text-gray-500">暂无租户数据</p>
                <a href="{{ route('admin.tenants.create') }}"
                   class="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                    创建第一个租户
                </a>
            </div>
        @endif
    </div>
</div>
@endsection
