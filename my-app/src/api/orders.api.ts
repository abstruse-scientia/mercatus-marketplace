import { apiClient } from "./client";
import type {
  OrderResponse,
  PlaceOrderRequest,
  PageResponse,
  OrderSummary,
  PaymentResponse,
} from "../types";

export const ordersApi = {
  placeOrder: async (data: PlaceOrderRequest) => {
    const response = await apiClient.post<OrderResponse>("/order/place", data);
    return response.data;
  },
  getOrders: async (params?: { page?: number; size?: number; status?: string }) => {
    const response = await apiClient.get<PageResponse<OrderSummary>>("/order", {
      params,
    });
    return response.data;
  },
  getOrderById: async (orderId: number) => {
    const response = await apiClient.get<OrderResponse>(`/order/${orderId}`);
    return response.data;
  },
  cancelOrder: async (orderId: number) => {
    const response = await apiClient.patch<OrderResponse>(
      `/order/${orderId}/cancel`,
    );
    return response.data;
  },
  initiatePayment: async (orderId: number) => {
    const response = await apiClient.post<PaymentResponse>(
      `/order/${orderId}/pay`,
    );
    return response.data;
  },
};
