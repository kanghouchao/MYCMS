import { centralApi, authApi } from '@/services/central/api';
import apiClient from '@/lib/client';

jest.mock('@/lib/client', () => ({
  __esModule: true,
  default: {
    get: jest.fn(async (url: string) => ({ data: { url } })),
    post: jest.fn(async (_url: string, _data?: any) => ({ data: {} })),
    put: jest.fn(async (_url: string, _data?: any) => ({ data: {} })),
    delete: jest.fn(async (_url: string) => ({ data: {} })),
  },
}));

describe('central api', () => {
  it('getList delegates to /central/tenants', async () => {
    const res = await centralApi.getList({ page: 1 });
    expect(res).toHaveProperty('url', '/central/tenants');
  });

  it('login returns data', async () => {
    const res = await authApi.login({ username: 'a', password: 'b' } as any);
    expect(res).toEqual({});
  });
});
