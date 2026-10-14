import React, { useState, useEffect } from "react";
import { Plus } from "lucide-react";
import { addressesApi } from "../api/addresses.api";
import AddressCard from "./AddressCard";
import AddressForm from "./AddressForm";
import type { Address, AddressRequest } from "../types";
import toast from "react-hot-toast";

const AddressBook: React.FC = () => {
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  
  // Form State
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState<Address | null>(null);
  const [isSaving, setIsSaving] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [settingDefaultId, setSettingDefaultId] = useState<number | null>(null);

  const fetchAddresses = async () => {
    try {
      setIsLoading(true);
      const data = await addressesApi.getAddresses();
      // Sort so default is at the top
      setAddresses(
        data.sort((a, b) => (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0))
      );
    } catch (error) {
      toast.error("Failed to load addresses");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAddresses();
  }, []);

  const handleOpenForm = (address?: Address) => {
    setEditingAddress(address || null);
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setEditingAddress(null);
  };

  const handleSaveAddress = async (data: AddressRequest) => {
    try {
      setIsSaving(true);
      if (editingAddress) {
        await addressesApi.updateAddress(editingAddress.addressId, data);
        toast.success("Address updated successfully");
      } else {
        await addressesApi.addAddress(data);
        toast.success("Address added successfully");
      }
      handleCloseForm();
      fetchAddresses();
    } catch (error) {
      toast.error(
        editingAddress ? "Failed to update address" : "Failed to add address"
      );
    } finally {
      setIsSaving(false);
    }
  };

  const handleDeleteAddress = async (addressId: number) => {
    try {
      setDeletingId(addressId);
      await addressesApi.deleteAddress(addressId);
      toast.success("Address deleted");
      fetchAddresses();
    } catch (error) {
      toast.error("Failed to delete address");
    } finally {
      setDeletingId(null);
    }
  };

  const handleSetDefault = async (addressId: number, currentIsDefault: boolean) => {
    if (currentIsDefault) return;
    try {
      setSettingDefaultId(addressId);
      // To set as default, we need to update the address with isDefault = true
      // The API requires the full AddressRequest, so we find it first
      const addressToUpdate = addresses.find((a) => a.addressId === addressId);
      if (!addressToUpdate) return;

      const requestPayload: AddressRequest = {
        fullName: addressToUpdate.fullName,
        mobileNumber: addressToUpdate.mobileNumber,
        flatHouse: addressToUpdate.flatHouse,
        area: addressToUpdate.area,
        landmark: addressToUpdate.landmark,
        pincode: addressToUpdate.pincode,
        city: addressToUpdate.city,
        state: addressToUpdate.state,
        country: addressToUpdate.country,
        isDefault: true, // Force to true
      };

      await addressesApi.updateAddress(addressId, requestPayload);
      toast.success("Default address updated");
      fetchAddresses();
    } catch (error) {
      toast.error("Failed to set default address");
    } finally {
      setSettingDefaultId(null);
    }
  };

  if (isLoading && addresses.length === 0) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <span className="text-lg text-neutral-500">Loading addresses...</span>
      </div>
    );
  }

  return (
    <div className="flex flex-col lg:flex-row gap-8 w-full">
      
      {/* Left Pane: Address List */}
      <div className={`flex-1 flex flex-col transition-all duration-300 ${isFormOpen ? "lg:w-7/12" : "w-full"}`}>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
          <div>
            <h1 className="text-2xl font-bold text-neutral-900 dark:text-white">
              Address Book
            </h1>
            <p className="text-sm text-neutral-600 dark:text-neutral-400 mt-1">
              Manage your saved addresses for a faster checkout experience.
            </p>
          </div>
          
          {/* Add New Button (hidden on mobile if form is open to save space, but visible on desktop) */}
          <button
            onClick={() => handleOpenForm()}
            className={`inline-flex items-center gap-2 px-5 py-2.5 bg-black text-white dark:bg-white dark:text-black rounded-lg text-sm font-medium hover:bg-neutral-800 dark:hover:bg-neutral-200 transition-colors ${isFormOpen ? 'hidden lg:inline-flex' : 'inline-flex'}`}
          >
            <Plus className="w-4 h-4" />
            Add New Address
          </button>
        </div>

        {addresses.length === 0 ? (
          <div className="flex flex-col items-center justify-center p-12 border border-dashed border-neutral-300 dark:border-neutral-800 rounded-xl bg-neutral-50 dark:bg-neutral-900/50">
            <div className="w-16 h-16 bg-neutral-200 dark:bg-neutral-800 rounded-full flex items-center justify-center mb-4">
              <Plus className="w-8 h-8 text-neutral-400" />
            </div>
            <h3 className="text-lg font-medium text-neutral-900 dark:text-white mb-2">No addresses saved</h3>
            <p className="text-neutral-500 text-center max-w-sm mb-6">
              You haven't saved any addresses yet. Add one now to make your next checkout perfectly smooth.
            </p>
            <button
              onClick={() => handleOpenForm()}
              className="inline-flex items-center gap-2 px-5 py-2.5 bg-black text-white dark:bg-white dark:text-black rounded-lg text-sm font-medium hover:bg-neutral-800 dark:hover:bg-neutral-200 transition-colors"
            >
              <Plus className="w-4 h-4" />
              Add New Address
            </button>
          </div>
        ) : (
          <div className="flex flex-col gap-4">
            {addresses.map((address) => (
              <AddressCard
                key={address.addressId}
                address={address}
                onEdit={handleOpenForm}
                onDelete={handleDeleteAddress}
                onSetDefault={handleSetDefault}
                isDeleting={deletingId === address.addressId}
                isSettingDefault={settingDefaultId === address.addressId}
              />
            ))}
            <div className="mt-4 text-sm text-neutral-500 dark:text-neutral-500">
              Showing 1 to {addresses.length} of {addresses.length} addresses
            </div>
          </div>
        )}
      </div>

      {/* Right Pane / Side Panel: Address Form */}
      {isFormOpen && (
        <div className="fixed inset-0 z-50 lg:static lg:z-auto lg:w-5/12 xl:w-[480px] lg:h-[calc(100vh-8rem)] lg:sticky lg:top-24">
          {/* Backdrop for mobile */}
          <div 
            className="absolute inset-0 bg-black/40 backdrop-blur-sm lg:hidden"
            onClick={handleCloseForm}
          />
          {/* Form Container */}
          <div className="absolute right-0 top-0 bottom-0 w-full max-w-md bg-white dark:bg-neutral-900 lg:relative lg:max-w-none lg:h-full lg:bg-transparent animate-in slide-in-from-right-8 lg:animate-none">
            <AddressForm
              initialData={editingAddress}
              onClose={handleCloseForm}
              onSave={handleSaveAddress}
              isSaving={isSaving}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default AddressBook;
