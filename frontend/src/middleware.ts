import { NextRequest, NextResponse } from "next/server";

const ADMIN_DOMAINS = new Set(["oli-cms.test"]);

export const config = {
  matcher: ["/((?!api|_next/static|health|favicon.ico|.*\\..*).*)"],
};

export async function middleware(request: NextRequest) {
  const rawHost =
    request.headers.get("x-forwarded-host") ||
    request.headers.get("host") ||
    request.nextUrl.hostname;
  const hostname = rawHost.split(",")[0].trim().split(":")[0].toLowerCase();

  if (ADMIN_DOMAINS.has(hostname)) {
    const res = NextResponse.next();
    res.headers.set("x-mw-role", "central");
    return res;
  }

  const validationApiUrl =
    process.env.TENANT_VALIDATION_API_URL || "http://backend:8080/tenant";

  const url = validationApiUrl + `?domain=${encodeURIComponent(hostname)}`;
  const res = await fetch(url);
  const data = await res.json().catch(() => null);

  if (!res.ok || !data || !data.tenant_id) {
    return NextResponse.redirect(
      new URL(
        (process.env.APP_URL || "//oli-cms.test") + "/welcome",
        request.url
      )
    );
  }

  let token = request.cookies.get("token")?.value;
  if (!token) {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  const nextRes = NextResponse.next();
  nextRes.headers.set("x-mw-role", "tenant");
  nextRes.headers.set("x-mw-tenant-template", String(data.template_key));
  nextRes.headers.set("x-mw-tenant-id", String(data.tenant_id));
  nextRes.headers.set("x-mw-tenant-name", String(data.tenant_name));
  return nextRes;
}
