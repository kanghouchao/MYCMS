// API 响应基础类型
export interface ApiResponse<T = any> {
  success: boolean;
  message?: string;
  data?: T;
  errors?: any;
}

// 分页响应类型
export interface PaginatedResponse<T> {
  data: T[];
  current_page: number;
  from: number;
  last_page: number;
  per_page: number;
  to: number;
  total: number;
  first_page_url: string;
  last_page_url: string;
  next_page_url: string | null;
  prev_page_url: string | null;
}

// 管理员类型
export interface Admin {
  id: number;
  name: string;
  email: string;
  role: 'super_admin' | 'admin';
  is_active: boolean;
  created_at: string;
  updated_at?: string;
}

// 认证响应类型
export interface AuthResponse {
  admin: Admin;
  token: string;
}

// 租户类型
export interface Tenant {
  id: string;
  name: string;
  email: string;
  template_key?: string;
  domain: string;
  domains: string[];
  is_active: boolean;
  created_at: string;
  updated_at?: string;
}

// 创建租户请求类型
export interface CreateTenantRequest {
  name: string;
  domain: string;
  email: string;
  template_key?: string;
}

// 更新租户请求类型
export interface UpdateTenantRequest {
  name: string;
  email: string;
  template_key?: string;
}

// 统计数据类型
export interface DashboardStats {
  total_tenants: number;
  total_domains: number;
  monthly_tenants: number;
  active_domains: number;
}

// 租户统计数据类型
export interface TenantStats {
  total: number;
  active: number;
  inactive: number;
  pending: number;
}

// 登录请求类型
export interface LoginRequest {
  email: string;
  password: string;
}
