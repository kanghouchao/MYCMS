import { authApi } from '@/services/tenant/api';

jest.mock('@/lib/client', () => ({
  __esModule: true,
  default: {
    get: jest.fn(async (url: string) => ({ data: { ok: true, url } })),
    post: jest.fn(async (url: string, _data?: any) => ({
      data:
        url === '/tenant/register'
          ? {
              tenantDomain: 'tenant.example.com',
              loginUrl: 'https://tenant.example.com/login',
              tenantName: 'Tenant Example',
            }
          : url === '/tenant/login'
            ? { token: 'jwt-token', expires_at: 123 }
            : { ok: true, url },
    })),
  },
}));

describe('tenant api', () => {
  it('register returns data from /tenant/register', async () => {
    const res = await authApi.register({} as any);
    expect(res).toEqual({
      tenantDomain: 'tenant.example.com',
      loginUrl: 'https://tenant.example.com/login',
      tenantName: 'Tenant Example',
    });
  });
  it('login returns data from /tenant/login', async () => {
    const res = await authApi.login({} as any);
    expect(res).toEqual({ token: 'jwt-token', expires_at: 123 });
  });
  it('me calls /tenant/me', async () => {
    const res = await authApi.me();
    expect(res).toEqual({ ok: true, url: '/tenant/me' });
  });
});
