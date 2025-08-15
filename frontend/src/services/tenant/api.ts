import apiClient from '@/lib/client';
import {
  ApiResponse,
  RegisterRequest
} from '@/types/api';
import { register } from 'module';

export const tenantApi = {
    register: async (data: RegisterRequest): Promise<ApiResponse> => {
        const response = await apiClient.post('/user/register', data);
        return response.data;
    },
};
