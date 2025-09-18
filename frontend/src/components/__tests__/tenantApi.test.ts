import { authApi } from "@/services/tenant/api";

jest.mock("@/lib/client", () => ({
  __esModule: true,
  default: {
    get: jest.fn(async (url: string) => ({ data: { ok: true, url } })),
    post: jest.fn(async (url: string, _data?: any) => ({
      data: { ok: true, url },
    })),
  },
}));

describe("tenant api", () => {
  it("register calls /tenant/register", async () => {
    await expect(authApi.register({} as any)).resolves.toBeUndefined();
  });
  it("login returns data from /tenant/login", async () => {
    const res = await authApi.login({} as any);
    expect(res).toEqual({ ok: true, url: "/tenant/login" });
  });
  it("me calls /tenant/me", async () => {
    const res = await authApi.me();
    expect(res).toEqual({ ok: true, url: "/tenant/me" });
  });
});
