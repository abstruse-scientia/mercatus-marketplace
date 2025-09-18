import { useState, useEffect } from "react";
import { NavLink } from "react-router-dom";
import { ShoppingCart, Bell, User, Box, Sun, Moon } from "lucide-react";

export default function Header() {
  const [dark, setDark] = useState(false);

  useEffect(() => {
    if (dark) {
      document.documentElement.classList.add("dark");
    } else {
      document.documentElement.classList.remove("dark");
    }
  }, [dark]);

  return (
    <header className="sticky top-0 z-40 w-full border-b border-border bg-background/95 backdrop-blur transition-colors">
      <div className="mx-auto flex h-14 w-full items-center justify-between px-4">
        <div className="flex items-center gap-2">
          <span className="flex h-7 w-7 items-center justify-center rounded border border-muted">
            <Box className="h-4 w-4" />
          </span>
          <span className="font-semibold tracking-tight">Mercatus</span>
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
          <NavLink
            to="/login"
            className="hover:text-foreground/80 transition-colors"
          >
            Login
          </NavLink>
        </nav>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setDark(!dark)}
            className="inline-flex h-9 w-9 items-center justify-center rounded-full hover:bg-muted transition-colors"
          >
            {dark ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
          </button>
          <button className="inline-flex h-9 w-9 items-center justify-center rounded-full hover:bg-muted transition-colors">
            <ShoppingCart className="h-5 w-5" />
          </button>
          <button className="inline-flex h-9 w-9 items-center justify-center rounded-full hover:bg-muted transition-colors">
            <Bell className="h-5 w-5" />
          </button>
          <button className="inline-flex h-9 w-9 items-center justify-center rounded-full hover:bg-muted transition-colors">
            <User className="h-5 w-5" />
          </button>
        </div>
      </div>
    </header>
  );
}
