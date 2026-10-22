import { apiClient } from "./client";

export interface ResetPasswordRequest {
  accessToken: string;
  newPassword: string;
}

export const userApi = {
  forgotPassword: async (email: string) => {
    const response = await apiClient.post("/user/forgot-password", { email });
    return response.data;
  },

  resetPassword: async (request: ResetPasswordRequest) => {
    const response = await apiClient.post("/user/reset-password", request);
    return response.data;
  },

  changePassword: async (newPassword: string) => {
    const response = await apiClient.post("/user/change-password", null, {
      params: { newPassword },
    });
    return response.data;
  },
};
