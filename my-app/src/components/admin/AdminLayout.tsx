import { NavLink, Outlet } from "react-router-dom";
import { Package, Tags, ShoppingCart, LayoutDashboard, Users } from "lucide-react";

const AdminLayout = () => {
  const navItems = [
    { to: "/admin/products", label: "Products", icon: <Package className="w-5 h-5" /> },
    { to: "/admin/categories", label: "Categories", icon: <Tags className="w-5 h-5" /> },
    { to: "/admin/orders", label: "Orders", icon: <ShoppingCart className="w-5 h-5" /> },
    { to: "/admin/users", label: "Users", icon: <Users className="w-5 h-5" /> },
  ];

  return (
    <div className="min-h-[calc(100vh-3.5rem)] bg-background flex flex-col md:flex-row">
      {/* Sidebar */}
      <aside className="w-full md:w-64 bg-card border-r border-border shrink-0 h-auto md:h-[calc(100vh-3.5rem)] md:sticky md:top-14 z-10">
        <div className="p-6">
          <div className="flex items-center gap-3 mb-8 text-foreground">
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
                      ? "bg-primary text-primary-foreground"
                      : "text-muted-foreground hover:bg-accent hover:text-foreground"
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
