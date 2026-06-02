import { useEffect, useState } from "react";
import {
  NavLink,
  Link,
  useNavigate,
  createSearchParams,
} from "react-router-dom";
import { ShoppingCart, User, Box, Sun, Moon, Search } from "lucide-react";
import { useAuthStore } from "../store/authStore";
import { useCartStore } from "../store/cartStore";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "./ui/dropdown-menu";

export default function Header() {
  const [dark, setDark] = useState(false);
  const [query, setQuery] = useState("");
  const navigate = useNavigate();

  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

  const itemCount = useCartStore((state) => state.getItemCount());
  const fetchCart = useCartStore((state) => state.fetchCart);

  useEffect(() => {
    fetchCart();
  }, [fetchCart, isAuthenticated]);

  useEffect(() => {
    if (dark) {
      document.documentElement.classList.add("dark");
    } else {
      document.documentElement.classList.remove("dark");
    }
  }, [dark]);

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  const handleSearchSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const trimmed = query.trim();

    if (!trimmed) {
      navigate("/products");
      return;
    }

    navigate({
      pathname: "/products",
      search: `?${createSearchParams({ search: trimmed })}`,
    });
  };

  return (
    <header className="sticky top-0 z-40 w-full border-b border-border bg-background/95 backdrop-blur transition-colors">
      <div className="mx-auto grid h-14 w-full grid-cols-[1fr_auto_1fr] items-center px-4 md:px-6">
        {/* Left: Brand */}
        <div className="flex min-w-0 items-center gap-2">
          <Link to="/home" className="flex shrink-0 items-center gap-2">
            <span className="flex h-7 w-7 items-center justify-center rounded border border-border">
              <Box className="h-4 w-4" />
            </span>
            <span className="text-[15px] font-semibold tracking-tight">
              Mercatus
            </span>
          </Link>
        </div>

        {/* Center: Collection-focused nav */}
        <nav className="hidden md:flex items-center justify-center gap-8 text-sm font-medium">
          <NavLink
            to="/products"
            className={({ isActive }) =>
              `transition-colors ${
                isActive
                  ? "text-foreground"
                  : "text-foreground/70 hover:text-foreground"
              }`
            }
          >
            Collection
          </NavLink>

          <NavLink
            to="/products?sort=createdAt,desc"
            className="text-foreground/70 transition-colors hover:text-foreground"
          >
            New Arrivals
          </NavLink>
        </nav>

        {/* Right: Search / Theme / Profile / Cart */}
        <div className="flex items-center justify-end gap-1 md:gap-2">
          <form
            onSubmit={handleSearchSubmit}
            role="search"
            className="relative hidden lg:flex items-center group mr-4"
          >
            <Search className="absolute left-0 h-4 w-4 text-foreground/40 transition-colors group-focus-within:text-foreground" />
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="SEARCH"
              aria-label="Search products"
              spellCheck={false}
              className="h-8 w-32 md:w-48 bg-transparent pl-7 pr-0 text-sm font-medium text-foreground placeholder:text-[10px] placeholder:font-bold placeholder:uppercase placeholder:tracking-[0.2em] placeholder:text-foreground/40 focus:outline-none transition-colors rounded-none"
            />
          </form>
          {/* END SEARCH */}

          <button
            onClick={() => setDark(!dark)}
            className="inline-flex h-9 w-9 items-center justify-center rounded-full transition-colors hover:bg-muted"
            aria-label="Toggle theme"
          >
            {dark ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
          </button>

          {/* UNIFIED PROFILE DROPDOWN */}
          <DropdownMenu>
            <DropdownMenuTrigger className="inline-flex h-9 w-9 items-center justify-center rounded-none text-foreground/70 outline-none transition-colors hover:bg-muted hover:text-foreground focus:outline-none">
              <User className="h-5 w-5" />
            </DropdownMenuTrigger>

            <DropdownMenuContent
              align="end"
              className="w-56 rounded-none border border-border shadow-none"
            >
              {isAuthenticated ? (
                <>
                  <DropdownMenuLabel className="p-3 font-normal">
                    <div className="flex flex-col space-y-1">
                      <p className="text-sm font-medium leading-none">
                        {user?.username}
                      </p>
                      <p className="text-xs leading-none text-foreground/50">
                        {user?.email}
                      </p>
                    </div>
                  </DropdownMenuLabel>
                  <DropdownMenuSeparator className="bg-border" />
                  <DropdownMenuItem
                    className="cursor-pointer rounded-none py-2.5 text-xs font-semibold uppercase tracking-wider"
                    onClick={() => navigate("/account")}
                  >
                    Account
                  </DropdownMenuItem>
                  <DropdownMenuItem
                    className="cursor-pointer rounded-none py-2.5 text-xs font-semibold uppercase tracking-wider"
                    onClick={() => navigate("/orders")}
                  >
                    Orders
                  </DropdownMenuItem>
                  <DropdownMenuSeparator className="bg-border" />
                  <DropdownMenuItem
                    className="cursor-pointer rounded-none py-2.5 text-xs font-semibold uppercase tracking-wider"
                    onClick={handleLogout}
                  >
                    Log out
                  </DropdownMenuItem>
                </>
              ) : (
                <>
                  <DropdownMenuItem
                    className="cursor-pointer rounded-none py-2.5 text-xs font-semibold uppercase tracking-wider"
                    onClick={() => navigate("/login")}
                  >
                    Login
                  </DropdownMenuItem>
                  <DropdownMenuItem
                    className="cursor-pointer rounded-none py-2.5 text-xs font-semibold uppercase tracking-wider"
                    onClick={() => navigate("/register")}
                  >
                    Register
                  </DropdownMenuItem>
                </>
              )}
            </DropdownMenuContent>
          </DropdownMenu>

          <Link
            to="/cart"
            className="relative inline-flex h-9 w-9 items-center justify-center transition-colors hover:bg-muted"
            aria-label="Cart"
          >
            <ShoppingCart className="h-5 w-5" />
            {itemCount > 0 && (
              <span className="absolute -right-1 -top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-foreground px-1 text-[10px] font-bold leading-none text-background">
                {itemCount}
              </span>
            )}
          </Link>
        </div>
      </div>
    </header>
  );
}
