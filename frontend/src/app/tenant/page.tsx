"use client";

import { useEffect, useState } from "react";
import TenantPageWrapper from "@/components/TenantHomepage";

interface ShopInfo {
  domain: string;
  shop_id: string;
  shop_name: string;
}

export default function TenantPage() {
  const [shopInfo, setShopInfo] = useState<ShopInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchShopInfo = async () => {
      try {
        const hostname = window.location.hostname;
        const apiBase = (process.env.NEXT_PUBLIC_API_URL || "").replace(
          /\/$/,
          ""
        );
        const response = await fetch(`${apiBase}/shops/${hostname}`, {
          headers: { Accept: "application/json" },
        });

        if (response.status === 404) {
          setError("店舗が存在しません");
          return;
        }

        if (!response.ok) {
          setError("店舗情報の取得に失敗しました");
          return;
        }

        const data = await response.json();
        // data: { success, domain, shop_id, shop_name }
        if (data.success) {
          setShopInfo({
            domain: data.domain,
            shop_id: data.shop_id,
            shop_name: data.shop_name,
          });
        } else {
          setError("店舗情報が無効です");
        }
      } catch (err) {
        setError("ネットワークエラー");
        console.error("店舗情報取得時のエラー:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchShopInfo();
  }, []);

  if (loading) {
    return (
      <main className="flex min-h-screen flex-col items-center justify-center p-24">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
        <p className="mt-4 text-lg text-gray-600">店舗情報を読み込み中...</p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="flex min-h-screen flex-col items-center justify-center p-24">
        <div className="text-red-600 text-xl mb-4">エラー</div>
        <p className="text-gray-600">{error}</p>
      </main>
    );
  }

  if (!shopInfo) {
    return (
      <main className="flex min-h-screen flex-col items-center justify-center p-24">
        <div className="text-yellow-600 text-xl mb-4">店舗が見つかりません</div>
        <p className="text-gray-600">
          このドメインに対応する店舗情報はありません
        </p>
      </main>
    );
  }

  return <TenantPageWrapper />;
}
