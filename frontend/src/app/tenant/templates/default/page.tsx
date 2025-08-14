import { headers } from "next/headers";

export default function Page() {
  const shopId = headers().get("x-mw-shop-id");
  const shopName = headers().get("x-mw-shop-name");

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <h1 className="text-3xl font-bold mb-4 text-indigo-700">
        Default テンプレート
      </h1>
      <p className="text-gray-600">
        これはデフォルトのテンプレートページです。
      </p>
      <div className="mt-8 text-gray-800">
        <div>店铺ID: {shopId}</div>
        <div>店铺名称: {shopName}</div>
      </div>
    </main>
  );
}
