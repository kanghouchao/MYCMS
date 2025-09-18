import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { useTenantInfo } from "@/hooks/useTenantInfo";

function TestComp() {
  const { tenant, loading, error } = useTenantInfo();
  return (
    <div>
      <div data-testid="loading">{String(loading)}</div>
      <div data-testid="tenant">{tenant ? JSON.stringify(tenant) : "null"}</div>
      <div data-testid="error">{error ?? ""}</div>
    </div>
  );
}

describe("useTenantInfo", () => {
  const originalLocation = window.location;
  const setLocation = (hostname: string, pathname: string) => {
    Object.defineProperty(window, "location", {
      value: { ...originalLocation, hostname, pathname },
      configurable: true,
    });
  };
  afterEach(() => {
    Object.defineProperty(window, "location", { value: originalLocation });
    (global as any).fetch?.mockClear?.();
  });

  it("skips fetching on /central path", async () => {
    setLocation("oli-cms.test", "/central");
    (global as any).fetch = jest.fn();
    render(<TestComp />);
    await waitFor(() =>
      expect(screen.getByTestId("loading").textContent).toBe("false")
    );
    expect((global as any).fetch).not.toHaveBeenCalled();
    expect(screen.getByTestId("tenant").textContent).toBe("null");
    expect(screen.getByTestId("error").textContent).toBe("");
  });

  it("resolves tenant info when API returns success", async () => {
    setLocation("tenant.example.test", "/");
    (global as any).fetch = jest.fn(async () => ({
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        domain: "tenant.example.test",
        tenant_id: "1",
        tenant_name: "T1",
      }),
    }));
    render(<TestComp />);
    await waitFor(() =>
      expect(screen.getByTestId("loading").textContent).toBe("false")
    );
    const tenant = screen.getByTestId("tenant").textContent || "";
    expect(tenant).toContain("tenant.example.test");
    expect(tenant).toContain('"tenant_id":"1"');
  });

  it("handles 404 as tenant not found", async () => {
    setLocation("no.example.test", "/");
    (global as any).fetch = jest.fn(async () => ({ ok: false, status: 404 }));
    render(<TestComp />);
    await waitFor(() =>
      expect(screen.getByTestId("loading").textContent).toBe("false")
    );
    expect(screen.getByTestId("tenant").textContent).toBe("null");
    expect(screen.getByTestId("error").textContent).toBe("租户不存在");
  });
});
