import apiClient from '@/lib/api-client';
import {
  ApiResponse,
  LoginRequest,
  AuthResponse,
  Admin,
  Tenant,
  CreateTenantRequest,
  UpdateTenantRequest,
  TenantStats,
  PaginatedResponse
} from '@/types/api';

// 认证 API
export const authApi = {
  // 登录
  login: async (credentials: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiClient.post('/admin/login', credentials);
    return response.data;
  },

  // 获取当前管理员信息
  me: async (): Promise<ApiResponse<Admin>> => {
    const response = await apiClient.get('/admin/me');
    return response.data;
  },

  // 登出
  logout: async (): Promise<ApiResponse> => {
    const response = await apiClient.post('/admin/logout');
    return response.data;
  },
};

// 租户 API
export const tenantApi = {
  getList: async (params?: {
    page?: number;
    per_page?: number;
    search?: string;
  }): Promise<ApiResponse<PaginatedResponse<Tenant>>> => {
    const response = await apiClient.get('/admin/tenants', { params });
    return response.data;
  },
  getById: async (id: string): Promise<ApiResponse<Tenant>> => {
    const response = await apiClient.get(`/admin/tenants/${id}`);
    return response.data;
  },
  create: async (data: CreateTenantRequest): Promise<ApiResponse<Tenant>> => {
    const response = await apiClient.post('/admin/tenants', data);
    return response.data;
  },
  update: async (id: string, data: UpdateTenantRequest): Promise<ApiResponse<Tenant>> => {
    const response = await apiClient.put(`/admin/tenants/${id}`, data);
    return response.data;
  },
  delete: async (id: string): Promise<ApiResponse> => {
    const response = await apiClient.delete(`/admin/tenants/${id}`);
    return response.data;
  },
  getStats: async (): Promise<ApiResponse<TenantStats>> => {
    const response = await apiClient.get('/admin/tenants/stats');
    return response.data;
  },
};

// 健康检查 API
export const healthApi = {
  check: async (): Promise<ApiResponse> => {
    const response = await apiClient.get('/health');
    return response.data;
  },
};
