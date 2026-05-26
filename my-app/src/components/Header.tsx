import { useState, useEffect } from "react";
import { NavLink, Link, useNavigate } from "react-router-dom";
import { ShoppingCart, User, Box, Sun, Moon } from "lucide-react";
import { useAuthStore } from "../store/authStore";
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
  const navigate = useNavigate();

  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

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

  return (
    <header className="sticky top-0 z-40 w-full border-b border-border bg-background/95 backdrop-blur transition-colors">
      <div className="mx-auto flex h-14 w-full items-center justify-between px-4">
        <div className="flex items-center gap-2">
          <Link to="/home" className="flex items-center gap-2">
            <span className="flex h-7 w-7 items-center justify-center rounded border border-muted">
              <Box className="h-4 w-4" />
            </span>
            <span className="font-semibold tracking-tight">Mercatus</span>
          </Link>
        </div>
        <nav className="hidden md:flex gap-6 text-sm font-medium">
          <NavLink
            to="/home"
            className="hover:text-foreground/80 transition-colors"
          >
            Home
          </NavLink>
          <NavLink
            to="/about"
            className="hover:text-foreground/80 transition-colors"
          >
            About
          </NavLink>
          <NavLink
            to="/contact"
            className="hover:text-foreground/80 transition-colors"
          >
            Contact
          </NavLink>
          <NavLink
            to="/products"
            className="hover:text-foreground/80 transition-colors"
          >
            Products
          </NavLink>
        </nav>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setDark(!dark)}
            className="inline-flex h-9 w-9 items-center justify-center rounded-full hover:bg-muted transition-colors"
          >
            {dark ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
          </button>

          <Link
            to="/cart"
            className="inline-flex h-9 w-9 items-center justify-center rounded-full hover:bg-muted transition-colors"
          >
            <ShoppingCart className="h-5 w-5" />
          </Link>

          {isAuthenticated ? (
            <DropdownMenu>
              <DropdownMenuTrigger className="inline-flex h-9 w-9 items-center justify-center rounded-full hover:bg-muted transition-colors outline-none focus:outline-none">
                <User className="h-5 w-5" />
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel className="font-normal">
                  <div className="flex flex-col space-y-1">
                    <p className="text-sm font-medium leading-none">
                      {user?.username}
                    </p>
                    <p className="text-xs leading-none text-muted-foreground">
                      {user?.email}
                    </p>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => navigate("/account")}>
                  Account
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => navigate("/orders")}>
                  Orders
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={handleLogout}>
                  Log out
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <div className="flex items-center gap-2 text-sm font-medium ml-2">
              <Link
                to="/login"
                className="hover:text-foreground/80 transition-colors"
              >
                Login
              </Link>
              <span className="text-border">|</span>
              <Link
                to="/register"
                className="hover:text-foreground/80 transition-colors"
              >
                Register
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
