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

// 店铺类型
export interface Shop {
  id: string;
  name: string;
  email: string;
  template_key?: string; // 店铺模板键（替代 plan）
  domain: string;
  domains: string[];
  is_active: boolean;
  created_at: string;
  updated_at?: string;
}

// 兼容旧代码：之前使用 Tenant 命名，现在统一为 Shop；提供别名避免导入错误
export type Tenant = Shop;

// 创建店铺请求类型
export interface CreateShopRequest {
  name: string;
  domain: string;
  email: string;
  template_key?: string;
}

// 更新店铺请求类型
export interface UpdateShopRequest {
  name: string;
  email: string;
  template_key?: string;
}

// 统计数据类型
export interface DashboardStats {
  total_shops: number;
  total_domains: number;
  monthly_shops: number;
  active_domains: number;
}

// 店铺统计数据类型
export interface ShopStats {
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
