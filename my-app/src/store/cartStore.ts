import { create } from "zustand";
import { persist } from "zustand/middleware";
import { cartApi } from "../api/cart.api";
import type { CartResponse } from "../types";
import { useAuthStore } from "./authStore";
import axios from "axios";

type APIErrorResponse = {
  errorMessage?: string;
};

export interface CartState {
  cart: CartResponse | null;
  isLoading: boolean;
  error: string | null;

  fetchCart: () => Promise<void>;
  addItem: (productId: number, quantity: number) => Promise<void>;
  updateQuantity: (productId: number, quantity: number) => Promise<void>;
  removeItem: (productId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  getItemCount: () => number;
}



const getErrorMessage = (error: unknown, fallback: string): string => {
  if (axios.isAxiosError<APIErrorResponse>(error)) {
    return error.response?.data?.errorMessage ?? error.message ?? fallback;
  }

  if (error instanceof Error) {
    return error.message;
  }
  return fallback;
};

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      cart: null,
      isLoading: false,
      error: null,

      fetchCart: async () => {
        set({ isLoading: true, error: null });
        try {
          const userId = useAuthStore.getState().user?.userId;
          const cart = await cartApi.getCart({ userId });
          set({ cart, isLoading: false });
        } catch (error: unknown) {
          set({
            error: getErrorMessage(error, "Failed to fetch cart"),
            isLoading: false,
          });
        }
      },

      addItem: async (productId: number, quantity: number) => {
        set({ isLoading: true, error: null });
        try {
          const userId = useAuthStore.getState().user?.userId;
          const cart = await cartApi.addToCart(
            { productId, quantity },
            { userId },
          );
          set({ cart, isLoading: false });
        } catch (error: unknown) {
          set({
            error: getErrorMessage(error, "Failed to add item"),
            isLoading: false,
          });
          throw error;
        }
      },

      updateQuantity: async (productId: number, quantity: number) => {
        set({ isLoading: true, error: null });
        try {
          const userId = useAuthStore.getState().user?.userId;
          const cart = await cartApi.updateQuantity(productId, quantity, {
            userId,
          });
          set({ cart, isLoading: false });
        } catch (error: unknown) {
          set({
            error: getErrorMessage(error, "Failed to update quantity"),
            isLoading: false,
          });
          throw error;
        }
      },

      removeItem: async (productId: number) => {
        set({ isLoading: true, error: null });
        try {
          const userId = useAuthStore.getState().user?.userId;
          const cart = await cartApi.removeFromCart(productId, {
            userId,
          });
          set({ cart, isLoading: false });
        } catch (error: unknown) {
          set({
            error: getErrorMessage(error, "Failed to remove item"),
            isLoading: false,
          });
          throw error;
        }
      },

      clearCart: async () => {
        set({ isLoading: true, error: null });
        try {
          const userId = useAuthStore.getState().user?.userId;
          await cartApi.clearCart({ userId });
          set({ cart: null, isLoading: false });
        } catch (error: unknown) {
          set({
            error: getErrorMessage(error, "Failed to clear cart"),
            isLoading: false,
          });
          throw error;
        }
      },

      getItemCount: () => {
        const { cart } = get();
        return cart?.itemCount || 0;
      },
    }),
    {
      name: "cart-storage",
      partialize: (state) => ({}), // We don't need to persist anything anymore, the cookie handles it!
    },
  ),
);
