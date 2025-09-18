import Cookies from "js-cookie";
import apiClient from "@/lib/client";

jest.mock("js-cookie");

describe("apiClient request interceptor", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("adds Authorization when token exists", async () => {
    (Cookies.get as jest.Mock).mockReturnValue("abc123");
    const original = apiClient.defaults.adapter as any;
    apiClient.defaults.adapter = (async (config: any) => ({
      data: {},
      status: 200,
      statusText: "OK",
      headers: {},
      config,
    })) as any;

    const res = await apiClient.get("/central/me");
    expect(res.config.headers?.Authorization).toBe("Bearer abc123");

    apiClient.defaults.adapter = original;
  });

  it("propagates tenant headers when cookies exist", async () => {
    (Cookies.get as jest.Mock).mockImplementation((key: string) => {
      if (key === "token") return "t";
      if (key === "x-mw-role") return "tenant";
      if (key === "x-mw-tenant-id") return "42";
      return undefined;
    });

    const original = apiClient.defaults.adapter as any;
    apiClient.defaults.adapter = (async (config: any) => ({
      data: {},
      status: 200,
      statusText: "OK",
      headers: {},
      config,
    })) as any;

    const res = await apiClient.get("/tenant/me");
    expect((res.config.headers as any)["X-Role"]).toBe("tenant");
    expect((res.config.headers as any)["X-Tenant-ID"]).toBe("42");

    apiClient.defaults.adapter = original;
  });
});
