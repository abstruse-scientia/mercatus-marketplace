import React from "react";
import { NavLink, Outlet } from "react-router-dom";
import { Package, Tags, ShoppingCart, LayoutDashboard } from "lucide-react";

const AdminLayout: React.FC = () => {
  const navItems = [
    { to: "/admin/products", label: "Products", icon: <Package className="w-5 h-5" /> },
    { to: "/admin/categories", label: "Categories", icon: <Tags className="w-5 h-5" /> },
    { to: "/admin/orders", label: "Orders", icon: <ShoppingCart className="w-5 h-5" /> },
  ];

  return (
    <div className="min-h-screen bg-neutral-50 dark:bg-[#0a0a0a] flex flex-col md:flex-row pt-16">
      {/* Sidebar */}
      <aside className="w-full md:w-64 bg-white dark:bg-neutral-900 border-r border-neutral-200 dark:border-neutral-800 shrink-0 h-auto md:h-[calc(100vh-4rem)] md:sticky md:top-16 z-10">
        <div className="p-6">
          <div className="flex items-center gap-3 mb-8 text-neutral-900 dark:text-white">
            <LayoutDashboard className="w-6 h-6" />
            <h2 className="text-xl font-bold tracking-tight">Admin Portal</h2>
          </div>
          <nav className="flex flex-col gap-2">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
                    isActive
                      ? "bg-black text-white dark:bg-white dark:text-black"
                      : "text-neutral-600 dark:text-neutral-400 hover:bg-neutral-100 dark:hover:bg-neutral-800 hover:text-neutral-900 dark:hover:text-white"
                  }`
                }
              >
                {item.icon}
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 p-4 sm:p-8 overflow-x-hidden">
        <div className="max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default AdminLayout;
