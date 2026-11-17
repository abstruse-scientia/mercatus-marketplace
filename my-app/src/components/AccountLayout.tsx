import { Outlet, NavLink } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

const AccountLayout = () => {
  const user = useAuthStore((state) => state.user);

  return (
    <div className="w-full bg-background min-h-screen py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-5xl mx-auto space-y-8">
        
        {/* Header Section */}
        <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 border-b border-border pb-6">
          <div>
            <h1 className="text-3xl font-bold tracking-tight text-foreground">
              My Account
            </h1>
            <p className="text-sm text-muted-foreground mt-1">
              Manage your profile, orders, and addresses.
            </p>
          </div>
          {user && (
            <div className="text-sm text-muted-foreground">
              <span className="font-medium text-foreground">{user.username}</span>
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
                  ? "bg-primary text-primary-foreground"
                  : "bg-card text-muted-foreground hover:bg-accent hover:text-foreground border border-border"
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
                  ? "bg-primary text-primary-foreground"
                  : "bg-card text-muted-foreground hover:bg-accent hover:text-foreground border border-border"
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
                  ? "bg-primary text-primary-foreground"
                  : "bg-card text-muted-foreground hover:bg-accent hover:text-foreground border border-border"
              }`
            }
          >
            Orders
          </NavLink>
        </nav>

        {/* Content Area */}
        <div className="bg-card text-card-foreground border border-border rounded-2xl p-6 sm:p-8 shadow-sm">
          <Outlet />
        </div>
        
      </div>
    </div>
  );
};

export default AccountLayout;
