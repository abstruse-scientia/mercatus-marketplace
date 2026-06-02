import { apiClient } from "./client";
import type { CartResponse, AddToCartRequest } from "../types";

export interface CartParams {
  userId?: number;
}

export const cartApi = {
  getCart: async (params?: CartParams) => {
    const response = await apiClient.get<CartResponse>("/cart", { params });
    return response.data;
  },
  addToCart: async (data: AddToCartRequest, params?: CartParams) => {
    const response = await apiClient.post<CartResponse>("/cart/items", data, {
      params,
    });
    return response.data;
  },
  updateQuantity: async (
    productId: number,
    quantity: number,
    params?: CartParams,
  ) => {
    const response = await apiClient.patch<CartResponse>(
      "/cart/items",
      {
        productId,
        quantity,
      },
      { params },
    );
    return response.data;
  },
  removeFromCart: async (productId: number, params?: CartParams) => {
    const response = await apiClient.delete<CartResponse>(
      `/cart/items/${productId}`,
      { params },
    );
    return response.data;
  },
  clearCart: async (params?: CartParams) => {
    const response = await apiClient.delete<string>("/cart", { params });
    return response.data;
  },
};
