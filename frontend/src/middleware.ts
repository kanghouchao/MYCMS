import { NextRequest, NextResponse } from "next/server";

// 管理域名列表（所有这些域名/子域名用于后台）
const ADMIN_DOMAINS = new Set(["oli-cms.test"]);

export const config = {
  matcher: ["/((?!api|_next/static|health|favicon.ico|.*\\..*).*)"],
};

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  const rawHost = (
    request.headers.get("x-forwarded-host") ||
    request.headers.get("host") ||
    request.nextUrl.hostname
  )
    .split(",")[0]
    .trim();
  const hostname = rawHost.split(":")[0].toLowerCase();

  console.debug(`[Middleware] host=${hostname} path=${pathname}`);

  if (ADMIN_DOMAINS.has(hostname)) {
    const res = NextResponse.next();
    res.headers.set("x-mw-role", "admin");
    return res;
  }

  const validationApiUrl = process.env.TENANT_VALIDATION_API_URL;
  if (!validationApiUrl) {
    console.error("TENANT_VALIDATION_API_URL 未配置");
    return new NextResponse("租户校验服务未配置", { status: 500 });
  }
  try {
    const url = validationApiUrl + `?domain=${encodeURIComponent(hostname)}`;

    const res = await fetch(url);
    const data = await res.json().catch(() => null);

    if (res.ok) {
            const nextRes = NextResponse.next();
            nextRes.headers.set("x-mw-role", "tenant");
            nextRes.headers.set('x-mw-tenant-template', String(data.template_key));
            nextRes.headers.set('x-mw-shop-id', String(data.shop_id));
            nextRes.headers.set('x-mw-shop-name', String(data.shop_name));
            return nextRes;
    }

    if (res.status === 404) {
      return new NextResponse(null, { status: 404 });
    } else {
      console.warn("租户校验接口异常", res.statusText, data);
      return new NextResponse("租户校验失败", { status: 500 });
    }
  } catch (err) {
    console.error("租户校验接口异常", err);
    return new NextResponse("租户校验失败", { status: 500 });
  }
}
