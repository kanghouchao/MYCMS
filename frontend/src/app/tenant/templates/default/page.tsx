import TenantDashboard from '@/components/tenant/TenantDashboard';
import { headers } from 'next/headers';

export default async function Page() {
  const hdrs = await headers();
  const tenantId = hdrs.get('x-mw-tenant-id');
  const tenantName = hdrs.get('x-mw-tenant-name');

  return <TenantDashboard tenantId={tenantId} tenantName={tenantName} />;
}
