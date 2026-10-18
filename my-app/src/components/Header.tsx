import { useEffect, useState } from "react";
import {
  NavLink,
  Link,
  useNavigate,
  useSearchParams,
  createSearchParams,
} from "react-router-dom";
import { ShoppingCart, User, Box, Sun, Moon, Search, Menu } from "lucide-react";
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
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "./ui/sheet";

export default function Header() {
  const [dark, setDark] = useState(() => {
    const saved = localStorage.getItem("theme");
    if (saved) return saved == "dark";
    return window.matchMedia("prefered-color-scheme: dark").matches;
  });
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchParams] = useSearchParams();
  const [query, setQuery] = useState(searchParams.get("search") || "");
  const navigate = useNavigate();

  // Sync query state if URL changes externally
  useEffect(() => {
    setQuery(searchParams.get("search") || "");
  }, [searchParams]);

  useEffect(() => {
    localStorage.setItem("theme", dark ? "dark" : "light");
    // dark: boolean value, adds dark class to root if its present or removes it.
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);

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

  const isAdmin = Array.isArray(user?.roles)
    ? user?.roles.includes("ROLE_ADMIN")
    : typeof user?.roles === "string" &&
      (user?.roles as string).includes("ROLE_ADMIN");

  return (
    <header className="sticky top-0 z-40 w-full border-b border-border bg-background/95 backdrop-blur transition-colors">
      <div className="mx-auto flex h-14 w-full items-center justify-between px-4 md:grid md:grid-cols-[1fr_auto_1fr]   md:px-6">
        {/* Left: Brand & Mobile Menu */}
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
          <div className="hidden md:flex items-center gap-1 md:gap-2">
            <form
              onSubmit={handleSearchSubmit}
              role="search"
              className="relative hidden lg:flex items-center group mr-4"
            >
              <Search className="absolute left-0 h-4 w-4 text-foreground/40 transition-colors group-focus-within:text-foreground" />
              <input
                type="text"
                value={query}
                onChange={(e) => {
                  const value = e.target.value;
                  setQuery(value);
                  // Auto-clear search if input is completely emptied
                  if (value.trim() === "" && searchParams.get("search")) {
                    navigate("/products");
                  }
                }}
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
              {dark ? (
                <Sun className="h-5 w-5" />
              ) : (
                <Moon className="h-5 w-5" />
              )}
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
                    {user?.roles?.includes("ROLE_ADMIN") && (
                      <DropdownMenuItem
                        asChild
                        className="cursor-pointer rounded-none py-2.5 text-xs font-semibold uppercase tracking-wider"
                      >
                        <Link to="/admin">Admin Dashboard</Link>
                      </DropdownMenuItem>
                    )}
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

          <div className="md:hidden">
            <Sheet open={mobileMenuOpen} onOpenChange={setMobileMenuOpen}>
              <SheetTrigger asChild>
                <button
                  className="relative inline-flex h-9 w-9 items-center justify-center rounded border border-border text-foreground/70 hover:text-foreground shrink-0"
                  aria-label="Open mobile navigation menu"
                >
                  <Menu className="h-5 w-5" />
                  {/* Subtle notification dot if cart has items */}
                  {itemCount > 0 && (
                    <span className="absolute -top-1 -right-1 h-2.5 w-2.5 rounded-full bg-foreground" />
                  )}
                </button>
              </SheetTrigger>

              <SheetContent
                side="right"
                className="w-[300px] sm:w-[350px] p-0 flex flex-col justify-between overflow-y-auto border-border/40"
              >
                <div className="flex flex-col">
                  {/* Mobile Drawer Header */}
                  <SheetHeader className="p-4 border-b border-border text-left">
                    <div className="flex items-center gap-2">
                      <span className="flex h-7 w-7 items-center justify-center rounded border border-border">
                        <Box className="h-4 w-4" />
                      </span>
                      <SheetTitle className="text-base font-semibold tracking-tight">
                        Mercatus
                      </SheetTitle>
                    </div>
                    <SheetDescription className="sr-only">
                      Mobile navigation options
                    </SheetDescription>
                  </SheetHeader>

                  {/* Mobile Search */}
                  <div className="p-4 border-b border-border">
                    <form
                      onSubmit={(e) => {
                        handleSearchSubmit(e);
                        setMobileMenuOpen(false);
                      }}
                      role="search"
                      className="relative flex items-center"
                    >
                      <Search className="absolute left-2.5 h-4 w-4 text-foreground/40" />
                      <input
                        type="text"
                        value={query}
                        onChange={(e) => {
                          const val = e.target.value;
                          setQuery(val);
                          if (val.trim() === "" && searchParams.get("search")) {
                            navigate("/products");
                          }
                        }}
                        placeholder="SEARCH PRODUCTS..."
                        aria-label="Search products"
                        className="h-9 w-full rounded border border-border bg-muted/30 pl-9 pr-3 text-xs font-medium text-foreground placeholder:text-[10px] placeholder:font-bold placeholder:uppercase placeholder:tracking-[0.15em] placeholder:text-foreground/40 focus:outline-none focus:border-foreground"
                      />
                    </form>
                  </div>

                  {/* Mobile Navigation Links */}
                  <div className="flex flex-col py-2">
                    <Link
                      to="/home"
                      onClick={() => setMobileMenuOpen(false)}
                      className="px-5 py-2.5 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                    >
                      Home
                    </Link>
                    <Link
                      to="/products"
                      onClick={() => setMobileMenuOpen(false)}
                      className="px-5 py-2.5 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                    >
                      Collection
                    </Link>
                    <Link
                      to="/products?sort=createdAt,desc"
                      onClick={() => setMobileMenuOpen(false)}
                      className="px-5 py-2.5 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                    >
                      New Arrivals
                    </Link>

                    {/* Integrated Mobile Cart Link */}
                    <Link
                      to="/cart"
                      onClick={() => setMobileMenuOpen(false)}
                      className="flex items-center justify-between px-5 py-2.5 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                    >
                      <div className="flex items-center gap-2">
                        <ShoppingCart className="h-4 w-4" />
                        <span>Cart</span>
                      </div>
                      {itemCount > 0 && (
                        <span className="flex h-5 min-w-5 items-center justify-center rounded-full bg-foreground px-1.5 text-[10px] font-bold text-background">
                          {itemCount}
                        </span>
                      )}
                    </Link>

                    <Link
                      to="/about"
                      onClick={() => setMobileMenuOpen(false)}
                      className="px-5 py-2.5 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                    >
                      About
                    </Link>
                    <Link
                      to="/contact"
                      onClick={() => setMobileMenuOpen(false)}
                      className="px-5 py-2.5 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                    >
                      Contact
                    </Link>
                  </div>

                  {/* Account / User Section */}
                  <div className="border-t border-border mt-2 pt-2 flex flex-col">
                    <div className="px-5 py-1 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground">
                      Account
                    </div>
                    {isAuthenticated ? (
                      <>
                        <div className="px-5 py-2">
                          <p className="text-sm font-medium text-foreground">
                            {user?.username}
                          </p>
                          <p className="text-xs text-muted-foreground truncate">
                            {user?.email}
                          </p>
                        </div>
                        <Link
                          to="/account"
                          onClick={() => setMobileMenuOpen(false)}
                          className="px-5 py-2 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                        >
                          Account Details
                        </Link>
                        <Link
                          to="/orders"
                          onClick={() => setMobileMenuOpen(false)}
                          className="px-5 py-2 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                        >
                          My Orders
                        </Link>
                        {isAdmin && (
                          <Link
                            to="/admin"
                            onClick={() => setMobileMenuOpen(false)}
                            className="px-5 py-2 text-sm font-semibold text-primary hover:bg-muted/50 transition-colors"
                          >
                            Admin Dashboard
                          </Link>
                        )}
                        <button
                          onClick={() => {
                            setMobileMenuOpen(false);
                            handleLogout();
                          }}
                          className="text-left px-5 py-2 text-sm font-medium text-destructive hover:bg-destructive/10 transition-colors"
                        >
                          Log out
                        </button>
                      </>
                    ) : (
                      <>
                        <Link
                          to="/login"
                          onClick={() => setMobileMenuOpen(false)}
                          className="px-5 py-2 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                        >
                          Login
                        </Link>
                        <Link
                          to="/register"
                          onClick={() => setMobileMenuOpen(false)}
                          className="px-5 py-2 text-sm font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors"
                        >
                          Register
                        </Link>
                      </>
                    )}
                  </div>
                </div>

                {/* Bottom Drawer Bar: Dark Mode Switch */}
                <div className="p-4 border-t border-border flex items-center justify-between">
                  <span className="text-sm font-medium text-foreground">
                    Dark Mode
                  </span>
                  <button
                    onClick={() => setDark(!dark)}
                    className="inline-flex h-9 w-9 items-center justify-center rounded-full transition-colors hover:bg-muted"
                    aria-label="Toggle theme"
                  >
                    {dark ? (
                      <Sun className="h-5 w-5" />
                    ) : (
                      <Moon className="h-5 w-5" />
                    )}
                  </button>
                </div>
              </SheetContent>
            </Sheet>
          </div>
        </div>
      </div>
    </header>
  );
}
