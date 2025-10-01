import apiClient from '@/lib/client';
import { authApi, employeeApi } from '@/services/tenant/api';

jest.mock('@/lib/client', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
  },
}));

const mockedClient = apiClient as jest.Mocked<typeof apiClient>;

describe('tenant auth api', () => {
  beforeEach(() => {
    mockedClient.get.mockReset();
    mockedClient.post.mockReset();

    mockedClient.get.mockImplementation(async (url: string) => {
      if (url === '/tenant/me') {
        return { data: { ok: true, url } } as any;
      }
      if (url === '/tenant/employees') {
        return {
          data: [
            {
              id: 'emp-1',
              name: 'Alice',
              email: 'alice@example.com',
              phone: null,
              position: 'Manager',
              active: true,
              created_at: '2024-01-01T00:00:00Z',
              updated_at: '2024-01-02T00:00:00Z',
            },
          ],
        } as any;
      }
      return { data: { ok: true, url } } as any;
    });

    mockedClient.post.mockImplementation(async (url: string, payload?: any) => {
      if (url === '/tenant/register') {
        return {
          data: {
            tenantDomain: 'tenant.example.com',
            loginUrl: 'https://tenant.example.com/login',
            tenantName: 'Tenant Example',
          },
        } as any;
      }
      if (url === '/tenant/login') {
        return { data: { token: 'jwt-token', expires_at: 123 } } as any;
      }
      if (url === '/tenant/employees') {
        return {
          data: {
            id: 'emp-2',
            name: payload?.name ?? 'no-name',
            email: payload?.email ?? null,
            phone: payload?.phone ?? null,
            position: payload?.position ?? null,
            active: true,
            created_at: '2024-02-01T00:00:00Z',
            updated_at: '2024-02-01T00:00:00Z',
          },
        } as any;
      }
      return { data: { ok: true, url, payload } } as any;
    });
  });

  it('register returns data from /tenant/register', async () => {
    const res = await authApi.register({} as any);
    expect(res).toEqual({
      tenantDomain: 'tenant.example.com',
      loginUrl: 'https://tenant.example.com/login',
      tenantName: 'Tenant Example',
    });
    expect(mockedClient.post).toHaveBeenCalledWith('/tenant/register', {});
  });

  it('login returns data from /tenant/login', async () => {
    const res = await authApi.login({} as any);
    expect(res).toEqual({ token: 'jwt-token', expires_at: 123 });
    expect(mockedClient.post).toHaveBeenCalledWith('/tenant/login', {});
  });

  it('me calls /tenant/me', async () => {
    const res = await authApi.me();
    expect(res).toEqual({ ok: true, url: '/tenant/me' });
    expect(mockedClient.get).toHaveBeenCalledWith('/tenant/me');
  });
});

describe('tenant employee api', () => {
  beforeEach(() => {
    mockedClient.get.mockReset();
    mockedClient.post.mockReset();

    mockedClient.get.mockImplementation(async (url: string) => {
      if (url === '/tenant/employees') {
        return {
          data: [
            {
              id: 'emp-1',
              name: 'Alice',
              email: 'alice@example.com',
              phone: null,
              position: 'Manager',
              active: true,
              created_at: '2024-01-01T00:00:00Z',
              updated_at: '2024-01-02T00:00:00Z',
            },
          ],
        } as any;
      }
      return { data: { ok: true, url } } as any;
    });

    mockedClient.post.mockImplementation(async (url: string, payload?: any) => {
      if (url === '/tenant/employees') {
        return {
          data: {
            id: 'emp-2',
            name: payload?.name ?? 'no-name',
            email: payload?.email ?? null,
            phone: payload?.phone ?? null,
            position: payload?.position ?? null,
            active: true,
            created_at: '2024-02-01T00:00:00Z',
            updated_at: '2024-02-01T00:00:00Z',
          },
        } as any;
      }
      return { data: { ok: true, url, payload } } as any;
    });
  });

  it('list returns employees from /tenant/employees', async () => {
    const res = await employeeApi.list();
    expect(mockedClient.get).toHaveBeenCalledWith('/tenant/employees');
    expect(res).toHaveLength(1);
    expect(res[0].name).toBe('Alice');
  });

  it('create posts to /tenant/employees', async () => {
    const payload = { name: 'Bob', email: 'bob@example.com' };
    const res = await employeeApi.create(payload);
    expect(mockedClient.post).toHaveBeenCalledWith('/tenant/employees', payload);
    expect(res.name).toBe('Bob');
    expect(res.active).toBe(true);
  });
});
