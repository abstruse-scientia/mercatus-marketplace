import React, { useState, useEffect } from "react";
import { X } from "lucide-react";
import type { AddressRequest, Address } from "../types";

interface AddressFormProps {
  initialData?: Address | null;
  onClose: () => void;
  onSave: (data: AddressRequest) => Promise<void>;
  isSaving: boolean;
}

const AddressForm = ({
  initialData,
  onClose,
  onSave,
  isSaving,
}: AddressFormProps) => {
  const [formData, setFormData] = useState<AddressRequest>({
    fullName: "",
    mobileNumber: "",
    flatHouse: "",
    area: "",
    landmark: "",
    pincode: "",
    city: "",
    state: "",
    country: "India",
    isDefault: false,
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        fullName: initialData.fullName,
        mobileNumber: initialData.mobileNumber,
        flatHouse: initialData.flatHouse,
        area: initialData.area,
        landmark: initialData.landmark || "",
        pincode: initialData.pincode,
        city: initialData.city,
        state: initialData.state,
        country: initialData.country,
        isDefault: initialData.isDefault,
      });
    }
  }, [initialData]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value, type } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]:
        type === "checkbox" ? (e.target as HTMLInputElement).checked : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSave(formData);
  };

  return (
    <div className="flex flex-col h-full bg-card text-card-foreground border border-border rounded-xl overflow-hidden shadow-sm">
      <div className="flex items-center justify-between p-6 border-b border-border">
        <h2 className="text-lg font-semibold text-foreground">
          {initialData ? "Edit Address" : "Add New Address"}
        </h2>
        <button
          onClick={onClose}
          className="p-2 -mr-2 text-muted-foreground hover:text-foreground transition-colors"
        >
          <X className="w-5 h-5" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-6">
        <form id="address-form" onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <label className="block text-sm font-medium text-foreground">
                Full Name
              </label>
              <input
                type="text"
                name="fullName"
                required
                value={formData.fullName}
                onChange={handleChange}
                placeholder="Enter full name"
                className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all placeholder:text-muted-foreground"
              />
            </div>
            <div className="space-y-2">
              <label className="block text-sm font-medium text-foreground">
                Mobile Number
              </label>
              <input
                type="tel"
                name="mobileNumber"
                required
                value={formData.mobileNumber}
                onChange={handleChange}
                placeholder="Enter mobile number"
                className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all placeholder:text-muted-foreground"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-foreground">
              Flat / House No.
            </label>
            <input
              type="text"
              name="flatHouse"
              required
              value={formData.flatHouse}
              onChange={handleChange}
              placeholder="Enter flat, house no."
              className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all placeholder:text-muted-foreground"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-foreground">
              Area / Street
            </label>
            <input
              type="text"
              name="area"
              required
              value={formData.area}
              onChange={handleChange}
              placeholder="Enter area or street"
              className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all placeholder:text-muted-foreground"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-foreground">
              Landmark <span className="text-muted-foreground font-normal">(Optional)</span>
            </label>
            <input
              type="text"
              name="landmark"
              value={formData.landmark}
              onChange={handleChange}
              placeholder="Enter landmark"
              className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all placeholder:text-muted-foreground"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
            <div className="space-y-2">
              <label className="block text-sm font-medium text-foreground">
                Pincode
              </label>
              <input
                type="text"
                name="pincode"
                required
                value={formData.pincode}
                onChange={handleChange}
                placeholder="Enter pincode"
                className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all placeholder:text-muted-foreground"
              />
            </div>
            <div className="space-y-2">
              <label className="block text-sm font-medium text-foreground">
                City
              </label>
              <input
                type="text"
                name="city"
                required
                value={formData.city}
                onChange={handleChange}
                placeholder="Enter city"
                className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all placeholder:text-muted-foreground"
              />
            </div>
            <div className="space-y-2">
              <label className="block text-sm font-medium text-foreground">
                State
              </label>
              <select
                name="state"
                required
                value={formData.state}
                onChange={handleChange}
                className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all appearance-none"
              >
                <option value="" disabled>Select state</option>
                <option value="Andhra Pradesh">Andhra Pradesh</option>
                <option value="Arunachal Pradesh">Arunachal Pradesh</option>
                <option value="Assam">Assam</option>
                <option value="Bihar">Bihar</option>
                <option value="Chhattisgarh">Chhattisgarh</option>
                <option value="Goa">Goa</option>
                <option value="Gujarat">Gujarat</option>
                <option value="Haryana">Haryana</option>
                <option value="Himachal Pradesh">Himachal Pradesh</option>
                <option value="Jharkhand">Jharkhand</option>
                <option value="Karnataka">Karnataka</option>
                <option value="Kerala">Kerala</option>
                <option value="Madhya Pradesh">Madhya Pradesh</option>
                <option value="Maharashtra">Maharashtra</option>
                <option value="Manipur">Manipur</option>
                <option value="Meghalaya">Meghalaya</option>
                <option value="Mizoram">Mizoram</option>
                <option value="Nagaland">Nagaland</option>
                <option value="Odisha">Odisha</option>
                <option value="Punjab">Punjab</option>
                <option value="Rajasthan">Rajasthan</option>
                <option value="Sikkim">Sikkim</option>
                <option value="Tamil Nadu">Tamil Nadu</option>
                <option value="Telangana">Telangana</option>
                <option value="Tripura">Tripura</option>
                <option value="Uttar Pradesh">Uttar Pradesh</option>
                <option value="Uttarakhand">Uttarakhand</option>
                <option value="West Bengal">West Bengal</option>
                <option value="Andaman and Nicobar Islands">Andaman and Nicobar Islands</option>
                <option value="Chandigarh">Chandigarh</option>
                <option value="Dadra and Nagar Haveli and Daman and Diu">Dadra and Nagar Haveli and Daman and Diu</option>
                <option value="Delhi">Delhi</option>
                <option value="Jammu and Kashmir">Jammu and Kashmir</option>
                <option value="Ladakh">Ladakh</option>
                <option value="Lakshadweep">Lakshadweep</option>
                <option value="Puducherry">Puducherry</option>
              </select>
            </div>
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-foreground">
              Country
            </label>
            <select
              name="country"
              required
              value={formData.country}
              onChange={handleChange}
              className="w-full px-4 py-2.5 rounded-lg border border-border bg-background text-foreground focus:ring-2 focus:ring-ring focus:border-transparent outline-none transition-all appearance-none"
            >
              <option value="India">India</option>
            </select>
          </div>

          <div className="pt-2">
            <label className="flex items-start gap-3 cursor-pointer group">
              <div className="relative inline-flex items-center mt-0.5">
                <input
                  type="checkbox"
                  name="isDefault"
                  className="sr-only peer"
                  checked={formData.isDefault}
                  onChange={handleChange}
                />
                <div className="w-11 h-6 bg-muted peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-border after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary peer-checked:after:bg-primary-foreground"></div>
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-medium text-foreground">
                  Set as Default Address
                </span>
                <span className="text-xs text-muted-foreground mt-1">
                  This will be used as your primary shipping address.
                </span>
              </div>
            </label>
          </div>
        </form>
      </div>

      <div className="p-6 border-t border-border bg-muted/20 flex justify-end gap-3">
        <button
          type="button"
          onClick={onClose}
          disabled={isSaving}
          className="px-6 py-2.5 rounded-lg text-sm font-medium border border-border text-foreground hover:bg-accent transition-colors disabled:opacity-50"
        >
          Cancel
        </button>
        <button
          type="submit"
          form="address-form"
          disabled={isSaving}
          className="px-6 py-2.5 rounded-lg text-sm font-medium bg-primary text-primary-foreground hover:opacity-90 transition-opacity disabled:opacity-50 min-w-[120px]"
        >
          {isSaving ? "Saving..." : "Save Address"}
        </button>
      </div>
    </div>
  );
};

export default AddressForm;
