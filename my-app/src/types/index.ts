export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  slug: string;
  primaryImageUrl: string;
  categoryName: string;
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


export type OrderStatus = "PENDING" | "PROCESSING" | "SHIPPED" | "DELIVERED" | "CANCELLED";

export type OrderPaymentStatus = "PENDING" | "PAID" | "FAILED" | "REFUNDED";


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
  orderPaymentStatus: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED' | 'CANCELLED';
  orderStatus: 'CREATED' | 'PAYMENT_PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
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
