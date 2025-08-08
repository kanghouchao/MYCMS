import React from "react";
import { useShopInfo } from "@/hooks/useShopInfo";

// シンプルな店舗ホーム。最小限のフィールドのみ使用
interface SimpleShopInfo {
  shop_name: string;
  domain: string;
  shop_id: string;
}

interface ShopHomepageProps {
  shop: SimpleShopInfo;
}

function ShopHomepage({ shop }: ShopHomepageProps) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-50">
      <div className="container mx-auto px-4 py-20 max-w-3xl">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            ようこそ <span className="text-indigo-600">{shop.shop_name}</span>
          </h1>
          <p className="text-gray-600">ドメイン: {shop.domain}</p>
        </div>
        <div className="bg-white rounded-xl shadow-md p-8 border border-gray-100 text-center">
          <h2 className="text-2xl font-semibold text-gray-800 mb-4">
            サイト準備中
          </h2>
          <p className="text-gray-600 mb-6">
            この店舗サイトは現在構築中です。後ほどさらに多くのコンテンツと機能を提供します。
          </p>
          <div className="text-sm text-gray-500">Shop ID: {shop.shop_id}</div>
        </div>
      </div>
    </div>
  );
}

export default function TenantPageWrapper() {
  const { shop, loading, error } = useShopInfo();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-indigo-600 mx-auto mb-4"></div>
          <p className="text-lg text-gray-600">読み込み中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 via-white to-pink-50">
        <div className="text-center max-w-md p-6">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">
            店舗を読み込めません
          </h1>
          <p className="text-gray-600 mb-6">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-gray-900 transition-colors"
          >
            再読み込み
          </button>
        </div>
      </div>
    );
  }

  if (!shop) {
    return null; // 让其它路由继续处理
  }

  return (
    <ShopHomepage
      shop={{
        shop_name: shop.shop_name,
        domain: shop.domain,
        shop_id: shop.shop_id,
      }}
    />
  );
}
