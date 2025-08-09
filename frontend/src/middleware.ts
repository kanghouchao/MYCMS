import { NextRequest, NextResponse } from 'next/server';

// 管理域名列表（所有这些域名/子域名用于后台）
const ADMIN_DOMAINS = new Set([
  'oli-cms.test',
  'admin.oli-cms.test',
  'localhost',
  '127.0.0.1'
]);

/**
 * 判断是否静态或应跳过的路径
 */
function isBypassedPath(pathname: string): boolean {
  return (
    pathname.startsWith('/api/') ||
    pathname.startsWith('/_next/') ||
    pathname === '/favicon.ico' ||
    pathname === '/robots.txt' ||
    pathname === '/health' ||
    /\.[a-zA-Z0-9]+$/.test(pathname)
  );
}

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // 真实域名获取顺序：x-forwarded-host > host > nextUrl.hostname
  const forwardedHost = request.headers.get('x-forwarded-host');
  const hostHeader = request.headers.get('host');
  const rawHost = (forwardedHost || hostHeader || request.nextUrl.hostname).split(',')[0].trim();
  const hostname = rawHost.split(':')[0].toLowerCase();

  const isAdminDomain = ADMIN_DOMAINS.has(hostname);
  const isShopDomain = !isAdminDomain; // 简化：不在管理域名列表的都视为店铺域名

  // 调试日志
  console.log(`[Middleware] host=${hostname} path=${pathname} admin=${isAdminDomain} fwd=${forwardedHost}`);

  if (isBypassedPath(pathname)) {
    return NextResponse.next();
  }

  // 管理域名逻辑
  if (isAdminDomain) {
    // 根路径直接去到 admin（保持原有行为）
    if (pathname === '/') {
      const res = NextResponse.redirect(new URL('/admin/dashboard', request.url));
      res.headers.set('x-mw-role', 'admin');
      return res;
    }
    const res = NextResponse.next();
    res.headers.set('x-mw-role', 'admin');
    return res;
  }

  // 店铺域名逻辑
  // 1. 根路径重写到 /tenant （先让前端页面自己再请求租户信息，降低 edge 外部依赖）
  if (isShopDomain && pathname === '/') {
    const res = NextResponse.rewrite(new URL('/tenant', request.url));
    res.headers.set('x-mw-role', 'tenant');
    res.headers.set('x-mw-tenant-check', 'lazy-client');
    return res;
  }

  // 2. 可选：对非根路径做快速租户存在性校验（相对路径，交由 Traefik PathPrefix(`/api/`) 到后端）
  // 只在首次命中（不以 /admin 开头且不是静态）时校验一次
  if (isShopDomain && !pathname.startsWith('/tenant') && !pathname.startsWith('/admin')) {
    try {
  const apiUrl = new URL(`/api/shops/${hostname}`, request.url); // 相对同源
      const t0 = Date.now();
      const resp = await fetch(apiUrl, { headers: { 'Accept': 'application/json' }, method: 'GET' });
      const dt = Date.now() - t0;
  if (resp.status === 404) {
  console.log(`[Middleware] 店铺不存在 404 hostname=${hostname}`);
        const notFound = NextResponse.rewrite(new URL('/404', request.url));
        notFound.headers.set('x-mw-role', 'tenant');
        notFound.headers.set('x-mw-tenant-check', '404');
        return notFound;
      }
      if (!resp.ok) {
        console.log(`[Middleware] 校验失败 status=${resp.status}`);
        const err = NextResponse.rewrite(new URL('/500', request.url));
        err.headers.set('x-mw-role', 'tenant');
        err.headers.set('x-mw-tenant-check', 'error');
        return err;
      }
  const data = await resp.json().catch(() => null);
  const templateKey = data?.template_key || 'default';
  console.log(`[Middleware] 店铺快速校验成功 ${hostname} (${dt}ms), template=${templateKey}`);
  const res = NextResponse.next();
      res.headers.set('x-mw-role', 'tenant');
      res.headers.set('x-mw-tenant-check', 'ok');
  res.headers.set('x-tenant-template', templateKey);
      return res;
    } catch (e) {
      console.error('[Middleware] 校验异常', e);
      const err = NextResponse.rewrite(new URL('/500', request.url));
      err.headers.set('x-mw-role', 'tenant');
      err.headers.set('x-mw-tenant-check', 'exception');
      return err;
    }
  }

  // 默认放行（/tenant 页面内部会自行拉取信息）
  const res = NextResponse.next();
  res.headers.set('x-mw-role', 'tenant');
  res.headers.set('x-mw-tenant-check', pathname.startsWith('/tenant') ? 'lazy-client' : 'skipped');
  return res;
}

export const config = {
  matcher: [
    /*
     * 匹配所有路径，除了：
     * - api routes
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     * - 包含扩展名的文件
     */
    '/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)',
  ],
};
