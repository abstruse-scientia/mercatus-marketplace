import { apiClient } from "./client";
import type { CartResponse, AddToCartRequest } from "../types";

export const cartApi = {
  getCart: async (params?: { sessionId?: string; userId?: number }) => {
    const response = await apiClient.get<CartResponse>("/cart", { params });
    return response.data;
  },
  addToCart: async (data: AddToCartRequest) => {
    const response = await apiClient.post<CartResponse>("/cart/items", data);
    return response.data;
  },
  updateQuantity: async (productId: number, quantity: number) => {
    const response = await apiClient.patch<CartResponse>("/cart/items", {
      productId,
      quantity,
    });
    return response.data;
  },
  removeFromCart: async (productId: number) => {
    const response = await apiClient.delete<CartResponse>(
      `/cart/items/${productId}`,
    );
    return response.data;
  },
  clearCart: async () => {
    const response = await apiClient.delete<string>("/cart");
    return response.data;
  },
};
