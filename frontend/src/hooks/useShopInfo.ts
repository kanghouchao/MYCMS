import { useState, useEffect } from 'react';

export interface ResolvedShopInfo {
  domain: string;
  shop_id: string;
  shop_name: string;
}

// 统一通过中心端点 /api/shops/{domain} 解析店铺
export function useShopInfo() {
  const [shop, setShop] = useState<ResolvedShopInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const run = async () => {
      try {
        setLoading(true);
        setError(null);
        const path = window.location.pathname;
        if (path.startsWith('/admin')) { setShop(null); return; }
        const hostname = window.location.hostname;
        const apiBase = (process.env.NEXT_PUBLIC_API_URL || '').replace(/\/$/, '');
        const res = await fetch(`${apiBase}/shops/${hostname}`, { headers: { 'Accept': 'application/json' }});
        if (res.status === 404) { setShop(null); setError('店铺不存在'); return; }
        if (!res.ok) { setShop(null); setError('获取店铺失败'); return; }
        const data = await res.json();
        if (data.success) {
          setShop({ domain: data.domain, shop_id: data.shop_id, shop_name: data.shop_name });
        } else {
          setShop(null); setError('店铺数据无效');
        }
      } catch (e) {
        setShop(null); setError('网络错误');
      } finally { setLoading(false); }
    };
    run();
  }, []);

  return { shop, loading, error };
}
