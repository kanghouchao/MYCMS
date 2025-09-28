jest.mock('next/server', () => {
  class MockCookies {
    private store = new Map<string, { name: string; value: string }>();

    set(name: string, value: string) {
      this.store.set(name, { name, value });
    }

    get(name: string) {
      return this.store.get(name) ?? undefined;
    }
  }

  class MockNextResponse {
    public readonly cookies = new MockCookies();
    public readonly status: number;
    public readonly body?: string;

    constructor(body?: string, init?: { status?: number }) {
      this.body = body;
      this.status = init?.status ?? 200;
    }

    static next() {
      return new MockNextResponse(undefined, { status: 200 });
    }
  }

  class MockNextRequest {}

  return {
    NextResponse: MockNextResponse,
    NextRequest: MockNextRequest,
  };
});

import type { NextRequest } from 'next/server';
import { middleware } from './middleware';

describe('middleware routing contract', () => {
  const originalFetch = global.fetch;

  const createRequest = (url: string, headers: Record<string, string> = {}) => {
    const normalizedEntries: Array<[string, string]> = Object.entries(headers).map(
      ([key, value]) => [key.toLowerCase(), value]
    );
    const headerStore = new Map<string, string>(normalizedEntries);

    return {
      headers: {
        get: (name: string) => headerStore.get(name.toLowerCase()) ?? null,
      },
      cookies: {
        get: () => undefined,
        getAll: () => [],
        set: () => undefined,
        delete: () => undefined,
        has: () => false,
      } as unknown,
      nextUrl: new URL(url),
      url,
      page: undefined as never,
      ua: undefined as never,
    } as unknown as NextRequest;
  };

  afterEach(() => {
    global.fetch = originalFetch;
    jest.restoreAllMocks();
    delete process.env.TENANT_VALIDATION_API_URL;
  });

  it('marks admin domains as central without calling validation API', async () => {
    const fetchSpy = jest.fn();
    global.fetch = fetchSpy as unknown as typeof fetch;
    const request = createRequest('http://oli-cms.test/dashboard', {
      host: 'oli-cms.test',
    });

    const response = await middleware(request);

    expect(fetchSpy).not.toHaveBeenCalled();
    expect(response.cookies.get('x-mw-role')?.value).toBe('central');
    expect(response.status).toBe(200);
  });

  it('sets tenant cookies when validation succeeds', async () => {
    const payload = {
      id: 'tenant-123',
      name: 'Tenant One',
      template_key: 'dark-mode',
    };
    global.fetch = jest.fn().mockResolvedValue({
      json: () => Promise.resolve(payload),
    });
    process.env.TENANT_VALIDATION_API_URL = 'http://backend:8080/central/tenants';

    const request = createRequest('http://shop.example.test/home', {
      host: 'shop.example.test',
    });

    const response = await middleware(request);

    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(response.cookies.get('x-mw-role')?.value).toBe('tenant');
    expect(response.cookies.get('x-mw-tenant-id')?.value).toBe('tenant-123');
    expect(response.cookies.get('x-mw-tenant-name')?.value).toBe('Tenant One');
    expect(response.cookies.get('x-mw-tenant-template')?.value).toBe('dark-mode');
    expect(response.status).toBe(200);
  });

  it('returns 403 when validation fails', async () => {
    global.fetch = jest.fn().mockRejectedValue(new Error('network down'));

    const request = createRequest('http://unknown.test', {
      host: 'unknown.test',
    });

    const response = await middleware(request);

    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(response.status).toBe(403);
    expect(response.cookies.get('x-mw-role')).toBeUndefined();
  });
});
