import { apiClient } from "./client";
import type {
  AdminProduct,
  CreateProductRequest,
  UpdateProductRequest,
  Category,
  CreateCategoryRequest,
  UpdateCategoryRequest,
  ProductImage,
  AddProductImageRequest,
  AdminOrderSummary,
  AdminUserSummary,
  RoleObject,
  PageResponse,
  OrderStatus,
} from "../types";

export const adminApi = {
  // --- Products ---
  listProducts: async (page = 0, size = 10, sort?: string) => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    if (sort) params.append("sort", sort);
    const response = await apiClient.get<PageResponse<AdminProduct>>(`/admin/products?${params.toString()}`);
    return response.data;
  },
  getProduct: async (id: number) => {
    const response = await apiClient.get<AdminProduct>(`/admin/products/${id}`);
    return response.data;
  },
  createProduct: async (data: CreateProductRequest) => {
    const response = await apiClient.post<AdminProduct>("/admin/products", data);
    return response.data;
  },
  updateProduct: async (id: number, data: UpdateProductRequest) => {
    const response = await apiClient.put<AdminProduct>(`/admin/products/${id}`, data);
    return response.data;
  },
  deactivateProduct: async (id: number) => {
    const response = await apiClient.delete<string>(`/admin/products/${id}`);
    return response.data;
  },
  reactivateProduct: async (id: number) => {
    const response = await apiClient.post<string>(`/admin/products/${id}/reactivate`);
    return response.data;
  },
  listInactiveProducts: async (page = 0, size = 10) => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const response = await apiClient.get<PageResponse<AdminProduct>>(`/admin/products/inactive?${params.toString()}`);
    return response.data;
  },
  listProductsByCategory: async (categoryId: number, page = 0, size = 10) => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const response = await apiClient.get<PageResponse<AdminProduct>>(`/admin/products/category/${categoryId}?${params.toString()}`);
    return response.data;
  },

  // --- Product Images ---
  listImages: async (productId: number) => {
    const response = await apiClient.get<ProductImage[]>(`/admin/products/${productId}/images`);
    return response.data;
  },
  addImage: async (productId: number, data: AddProductImageRequest) => {
    const response = await apiClient.post<ProductImage>(`/admin/products/${productId}/images`, data);
    return response.data;
  },
  updateImage: async (productId: number, imageId: number, data: AddProductImageRequest) => {
    const response = await apiClient.put<ProductImage>(`/admin/products/${productId}/images/${imageId}`, data);
    return response.data;
  },
  deleteImage: async (productId: number, imageId: number) => {
    const response = await apiClient.delete<string>(`/admin/products/${productId}/images/${imageId}`);
    return response.data;
  },
  setPrimaryImage: async (productId: number, imageId: number) => {
    const response = await apiClient.put<ProductImage>(`/admin/products/${productId}/images/${imageId}/primary`);
    return response.data;
  },
  reorderImages: async (productId: number, imageIds: number[]) => {
    const response = await apiClient.put<ProductImage[]>(`/admin/products/${productId}/images/reorder`, imageIds);
    return response.data;
  },

  // --- Categories ---
  listCategories: async (page = 0, size = 20) => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const response = await apiClient.get<PageResponse<Category>>(`/admin/categories?${params.toString()}`);
    return response.data;
  },
  getCategory: async (id: number) => {
    const response = await apiClient.get<Category>(`/admin/categories/${id}`);
    return response.data;
  },
  createCategory: async (data: CreateCategoryRequest) => {
    const response = await apiClient.post<Category>("/admin/categories", data);
    return response.data;
  },
  updateCategory: async (id: number, data: UpdateCategoryRequest) => {
    const response = await apiClient.put<Category>(`/admin/categories/${id}`, data);
    return response.data;
  },
  deleteCategory: async (id: number) => {
    const response = await apiClient.delete<string>(`/admin/categories/${id}`);
    return response.data;
  },

  // --- Orders ---
  listAllOrders: async (page = 0, size = 10, status?: OrderStatus) => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    if (status) params.append("status", status);
    const response = await apiClient.get<PageResponse<AdminOrderSummary>>(`/admin/orders/all?${params.toString()}`);
    return response.data;
  },
  getOrder: async (id: number) => {
    const response = await apiClient.get<AdminOrderSummary>(`/admin/orders/${id}`);
    return response.data;
  },
  updateOrderStatus: async (id: number, status: OrderStatus) => {
    const response = await apiClient.patch<OrderStatus>(`/admin/orders/${id}/status`, { status });
    return response.data;
  },

  listUsers: async (page = 0, size = 10) => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const response = await apiClient.post<PageResponse<AdminUserSummary>>(`/admin/user/all?${params.toString()}`);
    return response.data;
  },
  deactivateUser: async (userId: number) => {
    const response = await apiClient.post<void>(`/admin/user/${userId}/deactivate`);
    return response.data;
  },
  reactivateUser: async (userId: number) => {
    const response = await apiClient.post<void>(`/admin/user/${userId}/reactivate`);
    return response.data;
  },
  updateUserRoles: async (userId: number, roles: RoleObject[]) => {
    const response = await apiClient.post<void>(`/admin/user/${userId}/roles`, roles);
    return response.data;
  },
  deleteUser: async (userId: number) => {
    const response = await apiClient.delete<void>(`/admin/user/${userId}`);
    return response.data;
  },
};
