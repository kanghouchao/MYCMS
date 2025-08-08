import axios from 'axios';
import Cookies from 'js-cookie';

// 创建API客户端实例
const apiClient = axios.create({
  baseURL: '/api',  // 使用相对路径，这样所有域名都可以使用同一个API路径
  timeout: 30000,
  withCredentials: true, // 支持跨域cookie
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'X-Requested-With': 'XMLHttpRequest',
  },
});

// 请求拦截器 - 添加认证令牌
apiClient.interceptors.request.use(
  (config) => {
    const token = Cookies.get('admin_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理认证错误
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // 清除无效的令牌
      Cookies.remove('admin_token');
      // 如果当前不在登录页面，重定向到登录页面
      if (typeof window !== 'undefined' && !window.location.pathname.includes('/login')) {
        window.location.href = '/admin/login';
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
