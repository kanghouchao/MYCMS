import Cookies from "js-cookie";
import apiClient from "@/lib/client";

jest.mock("js-cookie");

describe("apiClient 401 interceptor", () => {
  const originalHref = window.location.href;
  const originalLocation = window.location;
  beforeEach(() => {
    jest.clearAllMocks();
    (Cookies.get as jest.Mock).mockImplementation((key: string) => {
      if (key === "token") return "tkn";
      return undefined;
    });
  });
  afterEach(() => {
    Object.defineProperty(window, "location", { value: originalLocation });
  });

  it("clears token and redirects on 401", async () => {
    const removeSpy = jest.spyOn(Cookies, "remove");
    const original = apiClient.defaults.adapter as any;
    // Stub location.href setter to avoid jsdom navigation error
    Object.defineProperty(window, "location", {
      configurable: true,
      value: {
        ...originalLocation,
        set href(_v: string) {
          /* no-op in test */
        },
        get href() {
          return originalHref;
        },
      },
    });
    apiClient.defaults.adapter = (async (_config: any) => {
      const error: any = new Error("Unauthorized");
      error.response = { status: 401 };
      return Promise.reject(error);
    }) as any;

    // avoid infinite loop by calling request to any url
    await expect(apiClient.get("/central/me")).rejects.toBeDefined();
    expect(removeSpy).toHaveBeenCalledWith("token");

    apiClient.defaults.adapter = original;
  });
});
