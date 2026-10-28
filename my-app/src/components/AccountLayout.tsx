import { Outlet, NavLink } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

const AccountLayout = () => {
  const user = useAuthStore((state) => state.user);

  return (
    <div className="w-full bg-neutral-50 dark:bg-[#0a0a0a] min-h-screen py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-5xl mx-auto space-y-8">
        
        {/* Header Section */}
        <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 border-b border-neutral-200 dark:border-neutral-800 pb-6">
          <div>
            <h1 className="text-3xl font-bold tracking-tight text-neutral-900 dark:text-white">
              My Account
            </h1>
            <p className="text-sm text-neutral-500 mt-1">
              Manage your profile, orders, and addresses.
            </p>
          </div>
          {user && (
            <div className="text-sm text-neutral-600 dark:text-neutral-400">
              <span className="font-medium text-neutral-900 dark:text-neutral-200">{user.username}</span>
              <span className="mx-2">•</span>
              {user.email}
            </div>
          )}
        </div>

        {/* Horizontal Tab Strip */}
        <nav className="flex items-center gap-2 overflow-x-auto pb-2 hide-scrollbar">
          <NavLink
            to="/account/profile"
            className={({ isActive }) =>
              `px-4 py-2 text-sm font-medium rounded-full whitespace-nowrap transition-colors ${
                isActive
                  ? "bg-black text-white dark:bg-white dark:text-black"
                  : "bg-white text-neutral-600 hover:bg-neutral-100 dark:bg-neutral-900 dark:text-neutral-400 dark:hover:bg-neutral-800 border border-neutral-200 dark:border-neutral-800"
              }`
            }
          >
            Profile
          </NavLink>
          <NavLink
            to="/account/addresses"
            className={({ isActive }) =>
              `px-4 py-2 text-sm font-medium rounded-full whitespace-nowrap transition-colors ${
                isActive
                  ? "bg-black text-white dark:bg-white dark:text-black"
                  : "bg-white text-neutral-600 hover:bg-neutral-100 dark:bg-neutral-900 dark:text-neutral-400 dark:hover:bg-neutral-800 border border-neutral-200 dark:border-neutral-800"
              }`
            }
          >
            Addresses
          </NavLink>
          <NavLink
            to="/account/orders"
            className={({ isActive }) =>
              `px-4 py-2 text-sm font-medium rounded-full whitespace-nowrap transition-colors ${
                isActive
                  ? "bg-black text-white dark:bg-white dark:text-black"
                  : "bg-white text-neutral-600 hover:bg-neutral-100 dark:bg-neutral-900 dark:text-neutral-400 dark:hover:bg-neutral-800 border border-neutral-200 dark:border-neutral-800"
              }`
            }
          >
            Orders
          </NavLink>
        </nav>

        {/* Content Area */}
        <div className="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-2xl p-6 sm:p-8 shadow-sm">
          <Outlet />
        </div>
        
      </div>
    </div>
  );
};

export default AccountLayout;
