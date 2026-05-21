import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { User, LoginRequest, RegisterRequest } from "../types";
import { authApi } from "../api/auth.api";

// Shape for the Auth
interface AuthState {
  user: User | null;
  jwtToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;

  setAuth: (user: User, jwtToken: string, refreshToken: string) => void;
  setTokens: (jwtToken: string, refreshToken: string) => void;
  setUser: (user: User) => void;

  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshAuth: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      jwtToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,

      setAuth: (user, jwtToken, refreshToken) =>
        set({ user, jwtToken, refreshToken, isAuthenticated: !!jwtToken }),
      setTokens: (jwtToken, refreshToken) =>
        set({ jwtToken, refreshToken, isAuthenticated: !!jwtToken }),
      setUser: (user) => set({ user }),

      login: async (data: LoginRequest) => {
        set({ isLoading: true });
        try {
          const response = await authApi.login(data);
          set({
            user: response.user,
            jwtToken: response.jwtToken,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      register: async (data: RegisterRequest) => {
        set({ isLoading: true });
        try {
          const response = await authApi.register(data);
          set({
            user: response.user,
            jwtToken: response.jwtToken,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      logout: async () => {
        try {
          const { jwtToken } = get(); 
          if (jwtToken) {
            await authApi.logout();
          }
        } catch (error) {
          console.error("Logout API failed, continuing client logout.", error);
        } finally {
          set({
            user: null,
            jwtToken: null,
            refreshToken: null,
            isAuthenticated: false,
          });
        }
      },

      refreshAuth: async () => {
        const { refreshToken } = get();
        if (!refreshToken) return;

        try {
          const response = await authApi.refreshToken(refreshToken);
          set({
            jwtToken: response.jwtToken,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
          });
        } catch (error) {
          set({
            user: null,
            jwtToken: null,
            refreshToken: null,
            isAuthenticated: false,
          });
          throw error;
        }
      },
    }),
    {
      name: "auth-storage",
    },
  ),
);
