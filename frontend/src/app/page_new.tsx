"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";

export default function Home() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    // このトップページは管理画面のルーティングのみを処理
    // 店舗用ドメインは middleware により /tenant へリダイレクトされます
    if (!isLoading) {
      if (isAuthenticated) {
        router.push("/admin/dashboard");
      } else {
        router.push("/admin/login");
      }
    }
  }, [isAuthenticated, isLoading, router]);

  // ローディング表示
  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
      <p className="mt-4 text-lg text-gray-600">リダイレクト中...</p>
    </main>
  );
}
