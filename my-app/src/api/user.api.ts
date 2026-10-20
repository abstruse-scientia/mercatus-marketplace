import apiClient from "./client"
export const userApi = {
    forgotPassword: async(email: string) => {
        const response = await apiClient.get("user/forgot-password")
    }
}