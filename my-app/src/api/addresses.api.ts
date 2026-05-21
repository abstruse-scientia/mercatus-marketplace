import { apiClient } from "./client";
import type { Address, AddressRequest } from "../types";

export const addressesApi = {
  addAddress: async (data: AddressRequest) => {
    const response = await apiClient.post<Address>("/address", data);
    return response.data;
  },
  getAddresses: async () => {
    const response = await apiClient.get<Address[]>("/address");
    return response.data;
  },
  getDefaultAddress: async () => {
    const response = await apiClient.get<Address>("/address/default");
    return response.data;
  },
  updateAddress: async (addressId: number, data: AddressRequest) => {
    const response = await apiClient.put<Address>(
      `/address/${addressId}`,
      data,
    );
    return response.data;
  },
  deleteAddress: async (addressId: number) => {
    const response = await apiClient.delete<string>(`/address/${addressId}`);
    return response.data;
  },
};
