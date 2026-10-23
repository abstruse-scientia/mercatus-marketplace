
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
    <div className="border border-neutral-200 dark:border-neutral-800 rounded-xl p-6 relative flex flex-col gap-4">
      {/* Top Header Row */}
      <div className="flex justify-between items-start">
        <div className="h-6">
          {address.isDefault && (
            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded text-xs font-medium bg-black text-white dark:bg-white dark:text-black">
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
            <div className="w-11 h-6 bg-neutral-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-neutral-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:bg-neutral-700 peer-checked:bg-black dark:peer-checked:bg-white peer-checked:after:dark:bg-black"></div>
            <span className="ml-3 text-sm font-medium text-neutral-900 dark:text-neutral-100">
              {address.isDefault ? "Default" : "Set as Default"}
            </span>
          </label>
        </div>
      </div>

      {/* Address Details */}
      <div className="flex flex-col gap-1">
        <h3 className="text-base font-semibold text-neutral-900 dark:text-white">
          {address.fullName}
        </h3>
        <p className="text-sm text-neutral-600 dark:text-neutral-400 mb-2">
          {address.mobileNumber}
        </p>
        <div className="text-sm text-neutral-600 dark:text-neutral-400 leading-relaxed">
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
          className="inline-flex items-center gap-2 px-4 py-2 border border-neutral-200 dark:border-neutral-800 rounded-lg text-sm font-medium text-neutral-700 dark:text-neutral-300 hover:bg-neutral-50 dark:hover:bg-neutral-900 transition-colors"
        >
          <Pencil className="w-4 h-4" />
          Edit
        </button>
        <button
          onClick={() => onDelete(address.addressId)}
          disabled={isDeleting}
          className="inline-flex items-center gap-2 px-4 py-2 border border-neutral-200 dark:border-neutral-800 rounded-lg text-sm font-medium text-neutral-700 dark:text-neutral-300 hover:bg-red-50 hover:text-red-600 hover:border-red-200 dark:hover:bg-red-950/30 dark:hover:text-red-500 dark:hover:border-red-900/50 transition-colors disabled:opacity-50"
        >
          <Trash2 className="w-4 h-4" />
          {isDeleting ? "Deleting..." : "Delete"}
        </button>
      </div>
    </div>
  );
};

export default AddressCard;
