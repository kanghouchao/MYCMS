import apiClient from "@/lib/client";
import { RegisterRequest, LoginRequest, LoginResponse } from "@/types/api";

export const authApi = {
  register: async (data: RegisterRequest): Promise<void> => {
    await apiClient.post("/user/register", data);
  },
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post("/user/login", data);
    return response.data;
  },
  logout: async (): Promise<void> => {
    await apiClient.post("/user/logout");
  },
  me: async (): Promise<void> => {
    await apiClient.get("/user/me");
  },
};
