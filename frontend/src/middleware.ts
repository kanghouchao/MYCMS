import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const ADMIN_DOMAINS = new Set(['oli-cms.test']);

export const config = {
  // Match all paths except static assets and API routes
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next (Next.js internal assets and data)
     * - favicon.ico (favicon file)
     * - health (health check)
     */
    '/((?!api|_next|favicon.ico|health).*)',
  ],
};

export async function middleware(request: NextRequest) {
  // Log every invocation for visibility during debugging
  console.error('🔄 MIDDLEWARE CALLED! Path:', request.nextUrl.pathname);

  const rawHost =
    request.headers.get('x-forwarded-host') ||
    request.headers.get('host') ||
    request.nextUrl.hostname;
  const hostname = rawHost.split(',')[0].trim().split(':')[0].toLowerCase();

  console.error('🌐 Raw host:', rawHost);
  console.error('🏠 Processed hostname:', hostname);

  const isHttps = request.nextUrl.protocol === 'https:';

  const baseCookieOptions = {
    httpOnly: false,
    sameSite: 'lax' as const,
    secure: isHttps,
    path: '/',
  };

  if (ADMIN_DOMAINS.has(hostname)) {
    console.error('👑 Admin domain detected');
    const res = NextResponse.next();
    res.cookies.set('x-mw-role', 'central', baseCookieOptions);
    return res;
  }

  const validationApiUrl =
    process.env.TENANT_VALIDATION_API_URL || 'http://backend:8080/central/tenants';

  const url = validationApiUrl + `?domain=${encodeURIComponent(hostname)}`;
  console.error('🔍 Validating tenant with URL:', url);

  try {
    const res = await fetch(url);
    const data = await res.json().catch(() => null);
    console.error('📡 Tenant validation response:', data);

    const nextRes = NextResponse.next();
    nextRes.cookies.set('x-mw-role', 'tenant', baseCookieOptions);
    // Accept both legacy shape { valid, template_key, tenant_id, tenant_name }
    // and current shape { id, name, domain, email }
    if (data && typeof data === 'object') {
      const templateKey = String((data.template_key ?? 'default') || 'default');
      const tenantId = String(data.tenant_id ?? data.id ?? '');
      const tenantName = String(data.tenant_name ?? data.name ?? '');

      // If tenantId and tenantName exist or domain exists, treat as valid
      const isValid = Boolean(tenantId || tenantName || data.domain);

      if (isValid) {
        nextRes.cookies.set('x-mw-tenant-template', templateKey, baseCookieOptions);
        if (tenantId) {
          nextRes.cookies.set('x-mw-tenant-id', tenantId, baseCookieOptions);
        }
        if (tenantName) {
          nextRes.cookies.set('x-mw-tenant-name', tenantName, baseCookieOptions);
        }
        console.error('✅ Tenant recognized, cookies set');
      } else {
        console.error('❌ Tenant payload lacked required identifiers');
      }
    } else {
      console.error('❌ Invalid tenant or no data');
    }
    return nextRes;
  } catch (error) {
    console.error('🚨 Middleware error:', error);
    // Return a 403 Forbidden response to prevent unauthorized access on validation failure
    return new NextResponse('Forbidden: Tenant validation failed', {
      status: 403,
    });
  }
}
