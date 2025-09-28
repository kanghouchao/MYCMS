import { authApi, centralApi } from '@/services/central/api';

const mockGet = jest.fn();
const mockPost = jest.fn();
const mockPut = jest.fn();
const mockDelete = jest.fn();

jest.mock('@/lib/client', () => ({
  get: (...args: any[]) => mockGet(...args),
  post: (...args: any[]) => mockPost(...args),
  put: (...args: any[]) => mockPut(...args),
  delete: (...args: any[]) => mockDelete(...args),
}));

describe('central api wrappers', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('authApi.login calls post and returns data', async () => {
    mockPost.mockResolvedValueOnce({ data: { token: 't' } });
    const res = await authApi.login({ username: 'a', password: 'b' } as any);
    expect(res).toEqual({ token: 't' });
    expect(mockPost).toHaveBeenCalledWith('/central/login', {
      username: 'a',
      password: 'b',
    });
  });

  it('authApi.me calls get and returns data', async () => {
    mockGet.mockResolvedValueOnce({ data: { id: '1' } });
    const res = await authApi.me();
    expect(res).toEqual({ id: '1' });
    expect(mockGet).toHaveBeenCalledWith('/central/me');
  });

  it('authApi.logout calls post', async () => {
    mockPost.mockResolvedValueOnce({});
    await authApi.logout();
    expect(mockPost).toHaveBeenCalledWith('/central/logout');
  });

  it('centralApi basic CRUD and stats', async () => {
    mockGet.mockResolvedValueOnce({ data: { items: [] } });
    const list = await centralApi.getList({ page: 1 });
    expect(list).toEqual({ items: [] });
    expect(mockGet).toHaveBeenCalledWith('/central/tenants', {
      params: { page: 1 },
    });

    mockGet.mockResolvedValueOnce({ data: { id: '2' } });
    const byId = await centralApi.getById('2');
    expect(byId).toEqual({ id: '2' });
    expect(mockGet).toHaveBeenCalledWith('/central/tenants/2');

    mockPost.mockResolvedValueOnce({ data: { id: '3' } });
    const created = await centralApi.create({ name: 't' } as any);
    expect(created).toEqual({ id: '3' });
    expect(mockPost).toHaveBeenCalledWith('/central/tenants', { name: 't' });

    mockPut.mockResolvedValueOnce({ data: { id: '3', name: 'u' } });
    const updated = await centralApi.update('3', { name: 'u' } as any);
    expect(updated).toEqual({ id: '3', name: 'u' });
    expect(mockPut).toHaveBeenCalledWith('/central/tenants/3', { name: 'u' });

    mockDelete.mockResolvedValueOnce({});
    await centralApi.delete('3');
    expect(mockDelete).toHaveBeenCalledWith('/central/tenants/3');

    mockGet.mockResolvedValueOnce({ data: { total: 1 } });
    const stats = await centralApi.getStats();
    expect(stats).toEqual({ total: 1 });
    expect(mockGet).toHaveBeenCalledWith('/central/tenants/stats');
  });
});
