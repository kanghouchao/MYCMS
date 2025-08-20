import apiClient from '@/lib/client';
import {
  ApiResponse,
  RegisterRequest,
  LoginRequest,
} from '@/types/api';

export const authApi = {
  register: async (data: RegisterRequest): Promise<ApiResponse> => {
    const response = await apiClient.post('/user/register', data);
    return response.data;
  },
  login: async (data: LoginRequest): Promise<any> => {
    const response = await apiClient.post('/user/login', data);
    return response.data;
  },
  logout: async (): Promise<ApiResponse> => {
    const response = await apiClient.post('/user/logout');
    return response.data;
  },
  me: async (): Promise<ApiResponse> => {
    const response = await apiClient.get('/user/me');
    return response.data;
  },
};
