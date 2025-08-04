<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title', '管理后台') - CMS 管理系统</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <meta name="csrf-token" content="{{ csrf_token() }}">
</head>
<body class="bg-gray-100">
    <div class="min-h-screen flex">
        <!-- 侧边栏 -->
        <div class="bg-gray-800 text-white w-64 space-y-6 py-7 px-2 absolute inset-y-0 left-0 transform -translate-x-full md:relative md:translate-x-0 transition duration-200 ease-in-out" id="sidebar">
            <!-- Logo -->
            <div class="text-white flex items-center space-x-2 px-4">
                <i class="fas fa-cog text-2xl"></i>
                <span class="text-2xl font-extrabold">CMS 管理</span>
            </div>

            <!-- 导航菜单 -->
            <nav class="space-y-2">
                <a href="{{ route('admin.dashboard') }}"
                   class="text-white hover:bg-gray-700 hover:text-white group flex items-center px-2 py-2 text-sm font-medium rounded-md {{ request()->routeIs('admin.dashboard') ? 'bg-gray-900' : '' }}">
                    <i class="fas fa-tachometer-alt mr-3"></i>
                    仪表板
                </a>

                <a href="{{ route('admin.tenants.index') }}"
                   class="text-white hover:bg-gray-700 hover:text-white group flex items-center px-2 py-2 text-sm font-medium rounded-md {{ request()->routeIs('admin.tenants.*') ? 'bg-gray-900' : '' }}">
                    <i class="fas fa-users mr-3"></i>
                    租户管理
                </a>
            </nav>
        </div>

        <!-- 主内容区域 -->
        <div class="flex-1 flex flex-col overflow-hidden">
            <!-- 头部 -->
            <header class="bg-white shadow-sm border-b border-gray-200">
                <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div class="flex justify-between h-16">
                        <div class="flex items-center">
                            <!-- 移动端菜单按钮 -->
                            <button class="md:hidden" onclick="toggleSidebar()">
                                <i class="fas fa-bars text-gray-600"></i>
                            </button>
                            <h1 class="ml-4 text-xl font-semibold text-gray-900">@yield('page-title', '仪表板')</h1>
                        </div>

                        <!-- 用户菜单 -->
                        <div class="flex items-center space-x-4">
                            <div class="relative">
                                <button class="flex items-center text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500" onclick="toggleUserMenu()">
                                    <i class="fas fa-user-circle text-2xl text-gray-600"></i>
                                    <span class="ml-2 text-gray-700">{{ Auth::guard('admin')->user()->name }}</span>
                                    <i class="fas fa-chevron-down ml-1 text-gray-600"></i>
                                </button>

                                <!-- 下拉菜单 -->
                                <div id="userMenu" class="hidden absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50">
                                    <form method="POST" action="{{ route('admin.logout') }}">
                                        @csrf
                                        <button type="submit" class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                            <i class="fas fa-sign-out-alt mr-2"></i>退出登录
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            <!-- 主内容 -->
            <main class="flex-1 overflow-x-hidden overflow-y-auto bg-gray-100">
                <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                    <!-- 消息提示 -->
                    @if(session('success'))
                        <div class="mb-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative">
                            <i class="fas fa-check-circle mr-2"></i>
                            {{ session('success') }}
                            <button class="absolute top-0 bottom-0 right-0 px-4 py-3" onclick="this.parentElement.style.display='none'">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                    @endif

                    @if(session('error'))
                        <div class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
                            <i class="fas fa-exclamation-circle mr-2"></i>
                            {{ session('error') }}
                            <button class="absolute top-0 bottom-0 right-0 px-4 py-3" onclick="this.parentElement.style.display='none'">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                    @endif

                    @yield('content')
                </div>
            </main>
        </div>
    </div>

    <script>
        function toggleSidebar() {
            const sidebar = document.getElementById('sidebar');
            sidebar.classList.toggle('-translate-x-full');
        }

        function toggleUserMenu() {
            const userMenu = document.getElementById('userMenu');
            userMenu.classList.toggle('hidden');
        }

        // 点击外部关闭用户菜单
        document.addEventListener('click', function(event) {
            const userMenu = document.getElementById('userMenu');
            const userButton = event.target.closest('button');

            if (!userButton || !userButton.onclick.toString().includes('toggleUserMenu')) {
                userMenu.classList.add('hidden');
            }
        });

        // CSRF token 设置
        window.Laravel = {
            csrfToken: '{{ csrf_token() }}'
        };
    </script>
    @stack('scripts')
</body>
</html>
