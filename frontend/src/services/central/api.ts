import apiClient from "@/lib/client";
import {
  LoginRequest,
  LoginResponse,
  Admin,
  Tenant,
  CreateTenantRequest,
  UpdateTenantRequest,
  TenantStats,
  PaginatedResponse,
} from "@/types/api";

// 认证 API
export const authApi = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post("/admin/login", credentials);
    return response.data;
  },

  me: async (): Promise<Admin> => {
    const response = await apiClient.get("/admin/me");
    return response.data;
  },

  logout: async (): Promise<void> => {
    await apiClient.post("/admin/logout");
  },
};

export const centralApi = {
  getList: async (params?: {
    page?: number;
    per_page?: number;
    search?: string;
  }): Promise<PaginatedResponse<Tenant>> => {
    const response = await apiClient.get("/admin/tenants", { params });
    return response.data;
  },
  getById: async (id: string): Promise<Tenant> => {
    const response = await apiClient.get(`/admin/tenants/${id}`);
    return response.data;
  },
  create: async (data: CreateTenantRequest): Promise<Tenant> => {
    const response = await apiClient.post("/admin/tenants", data);
    return response.data;
  },
  update: async (id: string, data: UpdateTenantRequest): Promise<Tenant> => {
    const response = await apiClient.put(`/admin/tenants/${id}`, data);
    return response.data;
  },
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/admin/tenants/${id}`);
  },
  getStats: async (): Promise<TenantStats> => {
    const response = await apiClient.get("/admin/tenants/stats");
    return response.data;
  },
};
