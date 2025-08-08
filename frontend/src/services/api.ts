import apiClient from '@/lib/api-client';
import {
  ApiResponse,
  LoginRequest,
  AuthResponse,
  Admin,
  Shop,
  CreateShopRequest,
  UpdateShopRequest,
  DashboardStats,
  ShopStats,
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

// 店铺 API
export const shopApi = {
  getList: async (params?: {
    page?: number;
    per_page?: number;
    search?: string;
  }): Promise<ApiResponse<PaginatedResponse<Shop>>> => {
    const response = await apiClient.get('/admin/shops', { params });
    return response.data;
  },
  getById: async (id: string): Promise<ApiResponse<Shop>> => {
    const response = await apiClient.get(`/admin/shops/${id}`);
    return response.data;
  },
  create: async (data: CreateShopRequest): Promise<ApiResponse<Shop>> => {
    const response = await apiClient.post('/admin/shops', data);
    return response.data;
  },
  update: async (id: string, data: UpdateShopRequest): Promise<ApiResponse<Shop>> => {
    const response = await apiClient.put(`/admin/shops/${id}`, data);
    return response.data;
  },
  delete: async (id: string): Promise<ApiResponse> => {
    const response = await apiClient.delete(`/admin/shops/${id}`);
    return response.data;
  },
  getStats: async (): Promise<ApiResponse<ShopStats>> => {
    const response = await apiClient.get('/admin/shops/stats');
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

// 兼容现有代码：Tenants 页面目前引用 tenantApi，其语义与 shopApi 相同
// 若未来后端提供独立的租户（Tenant）端点，可在此替换为真实实现
export const tenantApi = shopApi;
