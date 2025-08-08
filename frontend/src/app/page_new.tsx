"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";

export default function Home() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    // 主页面现在只处理管理后台逻辑
    // 租户域名会在 middleware 中被重定向到 /tenant
    if (!isLoading) {
      if (isAuthenticated) {
        router.push("/admin/dashboard");
      } else {
        router.push("/admin/login");
      }
    }
  }, [isAuthenticated, isLoading, router]);

  // 显示加载状态
  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
      <p className="mt-4 text-lg text-gray-600">重定向中...</p>
    </main>
  );
}
