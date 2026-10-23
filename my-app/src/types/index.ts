export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  slug: string;
  primaryImageUrl: string;
  categoryName: string;
}

export interface CategorySummary {
  id: number;
  categoryName: string;
  slug: string;
  productCount: number;
  previewImageURL?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  first: boolean;
  last: boolean;
}

export interface User {
  userId: number;
  username: string;
  email: string;
  roles: string[];
}

export interface LoginRequest {
  userEmail: string;
  password?: string;
}

export interface LoginResponse {
  message: string;
  user: User;
  jwtToken: string;
  refreshToken: string;
}

export interface RegisterRequest {
  email: string;
  password?: string;
  confirmPassword?: string;
  userName: string;
}

export interface CartItem {
  id: number;
  productId: number;
  name: string;
  quantity: number;
  unitPrice: number;
  totalItemsPrice: number;
  primaryImageUrl?: string;
  categoryName?: string;
}

export interface CartResponse {
  items: CartItem[];
  itemCount: number;
  subtotal: number;
}

export interface AddToCartRequest {
  productId: number;
  quantity: number;
}

export type OrderStatus =
  | "CREATED"
  | "CONFIRMED"
  | "CANCELLED"
  | "SHIPPED"
  | "DELIVERED"
  | "RETURNED";

export type OrderPaymentStatus =
  | "PENDING"
  | "SUCCESS"
  | "FAILED"
  | "REFUNDED"
  | "CANCELLED";

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  primaryImageUrl?: string;
}

export interface OrderResponse {
  orderId: number;
  orderReference: string;
  orderPaymentStatus: OrderPaymentStatus;
  orderTotal: number;
  orderStatus: OrderStatus;
  placedAt: string;
  items: OrderItem[];
}

export interface OrderItemSummary {
  productId: number;
  productName: string;
  primaryImageUrl: string;
}

export interface OrderSummary {
  id: number;
  totalAmount: number;
  orderPaymentStatus: OrderPaymentStatus;
  orderStatus: OrderStatus;
  orderReference: string;
  createdAt: string;
  orderSummaryList?: OrderItemSummary[];
}

export interface PlaceOrderRequest {
  orderReference: string;
  addressId: number;
}

export interface AddressRequest {
  isDefault: boolean;
  fullName: string;
  mobileNumber: string;
  flatHouse: string;
  area: string;
  landmark?: string;
  pincode: string;
  city: string;
  state: string;
  country: string;
}

export interface Address extends AddressRequest {
  addressId: number;
}

export interface ErrorResponse {
  apiPath: string;
  status: number;
  errorMessage: string;
  errorTimestamp: string;
}

export interface PaymentResponse {
  orderId: string;
  amount: number;
  currency: string;
  paymentProvider: string;
}

// Admin Product (mirrors AdminProductResponseDto)
export interface AdminProduct {
  productId: number;
  name: string;
  description?: string;
  price: number;
  sku: string;
  slug: string;
  primaryImageUrl: string;
  categoryId: number;
  categoryName: string;
  isActive: boolean;
}

// Admin Product Requests
export interface CreateProductRequest {
  name: string;
  description?: string;
  price: number;
  sku: string;
  categoryId: number;
}

export interface UpdateProductRequest {
  name: string;
  description?: string;
  price: number;
  sku: string;
  slug?: string;
  categoryId: number;
  primaryImageUrl?: string;
}

// Category (mirrors CategoryResponseDto)
export interface Category {
  categoryId: number;
  categoryName: string;
  slug: string;
}

export interface CreateCategoryRequest {
  name: string;
}
export interface UpdateCategoryRequest {
  categoryName: string;
  slug: string;
}

// Product Image (mirrors ProductImageResponseDto)
export interface ProductImage {
  id: number;
  url: string;
  sortOrder: number;
  isPrimary: boolean;
}

export interface AddProductImageRequest {
  url: string;
  isPrimary?: boolean;
  sortOrder?: number;
}

// Admin Order (for the new admin endpoints)
export interface AdminOrderSummary extends OrderSummary {
  userName: string;
  userEmail: string;
}

// Order Status Update
export interface UpdateOrderStatusRequest {
  status: OrderStatus;
}


// Admin User Management
export interface RoleObject {
  id?: number;
  name: string;
}

export interface AdminUserSummary {
  userId: number;
  userName: string;
  email: string;
  roles: RoleObject[];
  isActive: boolean;
}
