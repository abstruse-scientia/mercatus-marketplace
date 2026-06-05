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
          await cartApi.addToCart(
            { productId, quantity },
            { userId },
          );
          await get().fetchCart();
        } catch (error: unknown) {
          set({
            error: getErrorMessage(error, "Failed to add item"),
            isLoading: false,
          });
          throw error;
        }
      },

      updateQuantity: async (productId: number, quantity: number) => {
        const { cart } = get();
        if (!cart) return;

        // 1. Save previous state for potential rollback
        const previousCart = JSON.parse(JSON.stringify(cart)) as CartResponse;

        // 2. Optimistically update local state
        const updatedItems = cart.items.map((item) => {
          if (item.productId === productId) {
            return {
              ...item,
              quantity: quantity,
              totalItemsPrice: item.unitPrice * quantity,
            };
          }
          return item;
        }).filter(item => item.quantity > 0);

        const newSubtotal = updatedItems.reduce((sum, item) => sum + item.totalItemsPrice, 0);
        const newItemCount = updatedItems.reduce((sum, item) => sum + item.quantity, 0);

        set({
          cart: {
            ...cart,
            items: updatedItems,
            subtotal: newSubtotal,
            itemCount: newItemCount,
          },
          error: null,
        });

        // 3. Make API call in background
        try {
          const userId = useAuthStore.getState().user?.userId;
          if (quantity === 0) {
            await cartApi.removeFromCart(productId, { userId });
          } else {
            await cartApi.updateQuantity(productId, quantity, { userId });
          }
          // Fetch server state quietly to ensure exact sync (e.g. dynamic discounts)
          get().fetchCart();
        } catch (error: unknown) {
          // 4. Rollback on failure
          set({
            cart: previousCart,
            error: getErrorMessage(error, "Failed to update quantity"),
          });
          throw error;
        }
      },

      removeItem: async (productId: number) => {
        const { cart } = get();
        if (!cart) return;

        const previousCart = JSON.parse(JSON.stringify(cart)) as CartResponse;

        const updatedItems = cart.items.filter((item) => item.productId !== productId);
        const newSubtotal = updatedItems.reduce((sum, item) => sum + item.totalItemsPrice, 0);
        const newItemCount = updatedItems.reduce((sum, item) => sum + item.quantity, 0);

        set({
          cart: {
            ...cart,
            items: updatedItems,
            subtotal: newSubtotal,
            itemCount: newItemCount,
          },
          error: null,
        });

        try {
          const userId = useAuthStore.getState().user?.userId;
          await cartApi.removeFromCart(productId, { userId });
          get().fetchCart();
        } catch (error: unknown) {
          set({
            cart: previousCart,
            error: getErrorMessage(error, "Failed to remove item"),
          });
          throw error;
        }
      },

      clearCart: async () => {
        set({ isLoading: true, error: null });
        try {
          const userId = useAuthStore.getState().user?.userId;
          await cartApi.clearCart({ userId });
          await get().fetchCart();
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
      partialize: () => ({}), // We don't need to persist anything anymore, the cookie handles it!
    },
  ),
);
