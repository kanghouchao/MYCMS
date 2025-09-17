import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

const ADMIN_DOMAINS = new Set(["oli-cms.test"]);

export const config = {
  // 匹配所有路径，除了静态资源和 API
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     * - health (health check)
     */
    "/((?!api|_next/static|_next/image|favicon.ico|health).*)",
  ],
};

export async function middleware(request: NextRequest) {
  // 强制输出到错误日志
  console.error("🔄 MIDDLEWARE CALLED! Path:", request.nextUrl.pathname);

  const rawHost =
    request.headers.get("x-forwarded-host") ||
    request.headers.get("host") ||
    request.nextUrl.hostname;
  const hostname = rawHost.split(",")[0].trim().split(":")[0].toLowerCase();

  console.error("🌐 Raw host:", rawHost);
  console.error("🏠 Processed hostname:", hostname);

  if (ADMIN_DOMAINS.has(hostname)) {
    console.error("👑 Admin domain detected");
    const res = NextResponse.next();
    res.cookies.set("x-mw-role", "central");
    return res;
  }

  const validationApiUrl =
    process.env.TENANT_VALIDATION_API_URL ||
    "http://backend:8080/central/tenants";

  const url = validationApiUrl + `?domain=${encodeURIComponent(hostname)}`;
  console.error("🔍 Validating tenant with URL:", url);

  try {
    const res = await fetch(url);
    const data = await res.json().catch(() => null);
    console.error("📡 Tenant validation response:", data);

    const nextRes = NextResponse.next();
    nextRes.cookies.set("x-mw-role", "tenant");
    if (data && data.valid) {
      nextRes.cookies.set(
        "x-mw-tenant-template",
        String(data.template_key || "")
      );
      nextRes.cookies.set("x-mw-tenant-id", String(data.tenant_id || ""));
      nextRes.cookies.set("x-mw-tenant-name", String(data.tenant_name || ""));
      console.error("✅ Valid tenant, cookies set");
    } else {
      console.error("❌ Invalid tenant or no data");
    }
    return nextRes;
  } catch (error) {
    console.error("🚨 Middleware error:", error);
    const nextRes = NextResponse.next();
    nextRes.cookies.set("x-mw-role", "tenant");
    return nextRes;
  }
}
