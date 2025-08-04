@extends('layouts.admin')

@section('title', '租户管理')
@section('page-title', '租户管理')

@section('content')
<div class="mb-6 flex justify-between items-center">
    <div>
        <h2 class="text-2xl font-bold text-gray-900">租户列表</h2>
        <p class="mt-1 text-sm text-gray-600">管理所有租户账户和域名配置</p>
    </div>
    <a href="{{ route('admin.tenants.create') }}"
       class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
        <i class="fas fa-plus mr-2"></i>
        创建租户
    </a>
</div>

<div class="bg-white shadow overflow-hidden sm:rounded-md">
    @if($tenants->count() > 0)
        <ul class="divide-y divide-gray-200">
            @foreach($tenants as $tenant)
                <li>
                    <div class="px-4 py-4 sm:px-6">
                        <div class="flex items-center justify-between">
                            <div class="flex items-center">
                                <div class="flex-shrink-0">
                                    <div class="h-10 w-10 rounded-full bg-indigo-100 flex items-center justify-center">
                                        <i class="fas fa-building text-indigo-600"></i>
                                    </div>
                                </div>
                                <div class="ml-4">
                                    <div class="flex items-center">
                                        <p class="text-sm font-medium text-gray-900">
                                            {{ $tenant->name ?? '未设置名称' }}
                                        </p>
                                        <span class="ml-2 px-2 py-1 text-xs font-medium rounded-full
                                            @if($tenant->plan === 'enterprise') bg-purple-100 text-purple-800
                                            @elseif($tenant->plan === 'premium') bg-blue-100 text-blue-800
                                            @else bg-gray-100 text-gray-800
                                            @endif">
                                            {{ ucfirst($tenant->plan ?? 'basic') }}
                                        </span>
                                    </div>
                                    <div class="mt-1">
                                        <p class="text-sm text-gray-600">
                                            <i class="fas fa-envelope mr-1"></i>
                                            {{ $tenant->email ?? '未设置邮箱' }}
                                        </p>
                                        @if($tenant->domains->count() > 0)
                                            <p class="text-sm text-gray-600 mt-1">
                                                <i class="fas fa-globe mr-1"></i>
                                                @foreach($tenant->domains as $domain)
                                                    <span class="inline-block bg-gray-100 rounded px-2 py-1 text-xs mr-1">
                                                        {{ $domain->domain }}
                                                    </span>
                                                @endforeach
                                            </p>
                                        @endif
                                    </div>
                                </div>
                            </div>
                            <div class="flex items-center space-x-2">
                                <span class="text-xs text-gray-500">
                                    {{ $tenant->created_at ? $tenant->created_at->format('Y-m-d') : '未知时间' }}
                                </span>
                                <div class="flex space-x-2">
                                    <a href="{{ route('admin.tenants.show', $tenant) }}"
                                       class="text-indigo-600 hover:text-indigo-900">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                    <a href="{{ route('admin.tenants.edit', $tenant) }}"
                                       class="text-yellow-600 hover:text-yellow-900">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <form method="POST" action="{{ route('admin.tenants.destroy', $tenant) }}"
                                          class="inline"
                                          onsubmit="return confirm('确定要删除这个租户吗？此操作不可恢复！')">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="text-red-600 hover:text-red-900">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </li>
            @endforeach
        </ul>

        <!-- 分页 -->
        <div class="bg-white px-4 py-3 border-t border-gray-200 sm:px-6">
            {{ $tenants->links() }}
        </div>
    @else
        <div class="text-center py-12">
            <i class="fas fa-users text-6xl text-gray-300 mb-4"></i>
            <h3 class="text-lg font-medium text-gray-900 mb-2">暂无租户</h3>
            <p class="text-gray-500 mb-6">开始创建您的第一个租户</p>
            <a href="{{ route('admin.tenants.create') }}"
               class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                <i class="fas fa-plus mr-2"></i>
                创建租户
            </a>
        </div>
    @endif
</div>
@endsection
