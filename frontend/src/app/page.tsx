"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";

export default function Home() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (isLoading) return;

    const adminDomains = new Set([
      "oli-cms.test",
      "admin.oli-cms.test",
      "localhost",
      "127.0.0.1",
    ]);

    const host = typeof window !== "undefined" ? window.location.hostname : "";

    // 管理者用ドメイン以外 => /tenant ページへ (middleware が処理しなかった場合のフォールバック)
    if (!adminDomains.has(host)) {
      router.replace("/tenant");
      return;
    }

    // 管理ドメインの処理
    if (isAuthenticated) {
      router.replace("/admin/dashboard");
    } else {
      router.replace("/admin/login");
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
