import { apiClient } from "./client";
import type { LoginRequest, LoginResponse, RegisterRequest } from "../types";

export const authApi = {
  login: async (data: LoginRequest) => {
    const response = await apiClient.post<LoginResponse>("/auth/login", data);
    return response.data;
  },
  register: async (data: RegisterRequest) => {
    const response = await apiClient.post<LoginResponse>(
      "/auth/register",
      data,
    );
    return response.data;
  },
  refreshToken: async (token: string) => {
    const response = await apiClient.post<LoginResponse>("/auth/refresh", {
      refreshToken: token,
    });
    return response.data;
  },
  logout: async () => {
    const response = await apiClient.post("/auth/logout");
    return response.data;
  },
};
