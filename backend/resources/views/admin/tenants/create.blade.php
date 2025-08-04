@extends('layouts.admin')

@section('title', '创建租户')
@section('page-title', '创建租户')

@section('content')
<div class="max-w-2xl mx-auto">
    <div class="mb-6">
        <h2 class="text-2xl font-bold text-gray-900">创建新租户</h2>
        <p class="mt-1 text-sm text-gray-600">填写以下信息来创建一个新的租户账户</p>
    </div>

    <form method="POST" action="{{ route('admin.tenants.store') }}" class="space-y-6">
        @csrf

        <div class="bg-white shadow rounded-lg p-6">
            <div class="grid grid-cols-1 gap-6">
                <!-- 租户名称 -->
                <div>
                    <label for="name" class="block text-sm font-medium text-gray-700">
                        租户名称 <span class="text-red-500">*</span>
                    </label>
                    <input type="text"
                           name="name"
                           id="name"
                           value="{{ old('name') }}"
                           required
                           class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 @error('name') border-red-300 @enderror"
                           placeholder="请输入租户名称">
                    @error('name')
                        <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                    @enderror
                </div>

                <!-- 域名 -->
                <div>
                    <label for="domain" class="block text-sm font-medium text-gray-700">
                        域名 <span class="text-red-500">*</span>
                    </label>
                    <div class="mt-1 flex rounded-md shadow-sm">
                        <input type="text"
                               name="domain"
                               id="domain"
                               value="{{ old('domain') }}"
                               required
                               class="flex-1 block w-full border-gray-300 rounded-l-md focus:ring-indigo-500 focus:border-indigo-500 @error('domain') border-red-300 @enderror"
                               placeholder="example">
                        <span class="inline-flex items-center px-3 rounded-r-md border border-l-0 border-gray-300 bg-gray-50 text-gray-500 text-sm">
                            .your-domain.com
                        </span>
                    </div>
                    <p class="mt-1 text-sm text-gray-500">
                        租户将通过此子域名访问系统
                    </p>
                    @error('domain')
                        <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                    @enderror
                </div>

                <!-- 邮箱地址 -->
                <div>
                    <label for="email" class="block text-sm font-medium text-gray-700">
                        联系邮箱 <span class="text-red-500">*</span>
                    </label>
                    <input type="email"
                           name="email"
                           id="email"
                           value="{{ old('email') }}"
                           required
                           class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 @error('email') border-red-300 @enderror"
                           placeholder="contact@example.com">
                    @error('email')
                        <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                    @enderror
                </div>

                <!-- 套餐选择 -->
                <div>
                    <label for="plan" class="block text-sm font-medium text-gray-700">
                        套餐类型 <span class="text-red-500">*</span>
                    </label>
                    <select name="plan"
                            id="plan"
                            required
                            class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 @error('plan') border-red-300 @enderror">
                        <option value="">请选择套餐</option>
                        <option value="basic" {{ old('plan') === 'basic' ? 'selected' : '' }}>
                            基础版 - 适合小型团队
                        </option>
                        <option value="premium" {{ old('plan') === 'premium' ? 'selected' : '' }}>
                            高级版 - 适合中型企业
                        </option>
                        <option value="enterprise" {{ old('plan') === 'enterprise' ? 'selected' : '' }}>
                            企业版 - 适合大型企业
                        </option>
                    </select>
                    @error('plan')
                        <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                    @enderror
                </div>
            </div>
        </div>

        <!-- 套餐对比 -->
        <div class="bg-white shadow rounded-lg p-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">套餐对比</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <!-- 基础版 -->
                <div class="border border-gray-200 rounded-lg p-4">
                    <h4 class="font-medium text-gray-900">基础版</h4>
                    <ul class="mt-2 text-sm text-gray-600 space-y-1">
                        <li><i class="fas fa-check text-green-500 mr-2"></i>5个用户</li>
                        <li><i class="fas fa-check text-green-500 mr-2"></i>10GB 存储</li>
                        <li><i class="fas fa-check text-green-500 mr-2"></i>基础功能</li>
                    </ul>
                </div>

                <!-- 高级版 -->
                <div class="border border-blue-200 rounded-lg p-4 bg-blue-50">
                    <h4 class="font-medium text-blue-900">高级版</h4>
                    <ul class="mt-2 text-sm text-blue-700 space-y-1">
                        <li><i class="fas fa-check text-blue-500 mr-2"></i>50个用户</li>
                        <li><i class="fas fa-check text-blue-500 mr-2"></i>100GB 存储</li>
                        <li><i class="fas fa-check text-blue-500 mr-2"></i>高级功能</li>
                        <li><i class="fas fa-check text-blue-500 mr-2"></i>API 访问</li>
                    </ul>
                </div>

                <!-- 企业版 -->
                <div class="border border-purple-200 rounded-lg p-4 bg-purple-50">
                    <h4 class="font-medium text-purple-900">企业版</h4>
                    <ul class="mt-2 text-sm text-purple-700 space-y-1">
                        <li><i class="fas fa-check text-purple-500 mr-2"></i>无限用户</li>
                        <li><i class="fas fa-check text-purple-500 mr-2"></i>1TB 存储</li>
                        <li><i class="fas fa-check text-purple-500 mr-2"></i>全部功能</li>
                        <li><i class="fas fa-check text-purple-500 mr-2"></i>定制开发</li>
                        <li><i class="fas fa-check text-purple-500 mr-2"></i>专属支持</li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- 按钮 -->
        <div class="flex justify-end space-x-4">
            <a href="{{ route('admin.tenants.index') }}"
               class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                取消
            </a>
            <button type="submit"
                    class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                <i class="fas fa-plus mr-2"></i>
                创建租户
            </button>
        </div>
    </form>
</div>
@endsection
