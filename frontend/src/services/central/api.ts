import apiClient from '@/lib/client';
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
  login: async (credentials: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiClient.post('/admin/login', credentials);
    return response.data;
  },

  me: async (): Promise<ApiResponse<Admin>> => {
    const response = await apiClient.get('/admin/me');
    return response.data;
  },

  logout: async (): Promise<ApiResponse> => {
    const response = await apiClient.post('/admin/logout');
    return response.data;
  },
};

export const centralApi = {
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
