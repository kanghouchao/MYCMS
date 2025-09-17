import apiClient from "@/lib/client";
import { RegisterRequest, LoginRequest, LoginResponse } from "@/types/api";

export const authApi = {
  register: async (data: RegisterRequest): Promise<void> => {
    await apiClient.post("/tenant/register", data);
  },
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post("/tenant/login", data);
    return response.data;
  },
  logout: async (): Promise<void> => {
    await apiClient.post("/tenant/logout");
  },
  me: async (): Promise<any> => {
    const response = await apiClient.get("/tenant/me");
    return response.data;
  },
};
