
import { Star, Pencil, Trash2 } from "lucide-react";
import type { Address } from "../types";

interface AddressCardProps {
  address: Address;
  onEdit: (address: Address) => void;
  onDelete: (addressId: number) => void;
  onSetDefault: (addressId: number, currentIsDefault: boolean) => void;
  isSettingDefault?: boolean;
  isDeleting?: boolean;
}

const AddressCard = ({
  address,
  onEdit,
  onDelete,
  onSetDefault,
  isSettingDefault,
  isDeleting,
}: AddressCardProps) => {
  return (
    <div className="border border-border bg-card text-card-foreground rounded-xl p-6 relative flex flex-col gap-4">
      {/* Top Header Row */}
      <div className="flex justify-between items-start">
        <div className="h-6">
          {address.isDefault && (
            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded text-xs font-medium bg-primary text-primary-foreground">
              <Star className="w-3.5 h-3.5 fill-current" />
              Default Address
            </span>
          )}
        </div>
        <div className="flex items-center gap-3">
          <label className="relative inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              className="sr-only peer"
              checked={address.isDefault}
              disabled={isSettingDefault || address.isDefault}
              onChange={() => onSetDefault(address.addressId, address.isDefault)}
            />
            <div className="w-11 h-6 bg-muted peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-border after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary peer-checked:after:bg-primary-foreground"></div>
            <span className="ml-3 text-sm font-medium text-foreground">
              {address.isDefault ? "Default" : "Set as Default"}
            </span>
          </label>
        </div>
      </div>

      {/* Address Details */}
      <div className="flex flex-col gap-1">
        <h3 className="text-base font-semibold text-foreground">
          {address.fullName}
        </h3>
        <p className="text-sm text-muted-foreground mb-2">
          {address.mobileNumber}
        </p>
        <div className="text-sm text-muted-foreground leading-relaxed">
          <p>{address.flatHouse}</p>
          <p>{address.area}</p>
          {address.landmark && <p>{address.landmark}</p>}
          <p>
            {address.city}, {address.state} - {address.pincode}
          </p>
          <p>{address.country}</p>
        </div>
      </div>

      {/* Actions */}
      <div className="flex items-center justify-end gap-3 mt-2">
        <button
          onClick={() => onEdit(address)}
          className="inline-flex items-center gap-2 px-4 py-2 border border-border rounded-lg text-sm font-medium text-foreground hover:bg-accent transition-colors"
        >
          <Pencil className="w-4 h-4" />
          Edit
        </button>
        <button
          onClick={() => onDelete(address.addressId)}
          disabled={isDeleting}
          className="inline-flex items-center gap-2 px-4 py-2 border border-border rounded-lg text-sm font-medium text-foreground hover:bg-destructive/10 hover:text-destructive hover:border-destructive/30 transition-colors disabled:opacity-50"
        >
          <Trash2 className="w-4 h-4" />
          {isDeleting ? "Deleting..." : "Delete"}
        </button>
      </div>
    </div>
  );
};

export default AddressCard;
