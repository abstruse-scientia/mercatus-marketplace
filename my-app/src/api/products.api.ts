import { apiClient } from "./client";
import type { PageResponse, Product } from "../types";

export const productsApi = {
  listProducts: async (params?: {
    page?: number;
    size?: number;
    sort?: string;
  }) => {
    const response = await apiClient.get<PageResponse<Product>>("/products", {
      params,
    });
    return response.data;
  },
  getProduct: async (id: number) => {
    const response = await apiClient.get<Product>(`/products/${id}`);
    return response.data;
  },
  searchProducts: async (
    query: string,
    params?: { page?: number; size?: number; sort?: string },
  ) => {
    const response = await apiClient.get<PageResponse<Product>>(
      "/products/search",
      { params: { query, ...params } },
    );
    return response.data;
  },

  listByCategory: async (
    categoryId: number,
    params?: { page?: number; size?: number; sort?: string },
  ) => {
    const response = await apiClient.get<PageResponse<Product>>(
      `/products/category/${categoryId}`,
      { params },
    );
    return response.data;
  },
  listProductsByCategoryName: async(
    categoryName: string,
    params?: {page?: number, size?: number; sort?: string},
  ) => {
    const response = await apiClient.get<PageResponse<Product>>(
      `/products/category/name/${encodeURIComponent(categoryName)}`,
      {params}
    );
    return response.data
  },
  getCategories: async() => {
    const response = await apiClient.get<string[]>("products/categories")
    return response.data
  }
};
