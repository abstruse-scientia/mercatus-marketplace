import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";
import toast from "react-hot-toast";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import cameraPic from "../assets/images/camera-pic.jpg";
import { useAuthStore } from "../store/authStore";
import type { AxiosError } from "axios";

export default function Login() {
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);
  const isLoading = useAuthStore((state) => state.isLoading);

  const [show, setShow] = useState(false);
  const [userEmail, setUserEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!userEmail || !password) {
      toast.error("Please fill in both email and password.");
      return;
    }

    try {
      await login({ userEmail, password });
      toast.success("Successfully logged in!");
      navigate("/home");
    } catch (err: unknown) {

      const error = err as AxiosError<{message?: string}>;
      const message = error.response?.data?.message ||
          "Failed to login. Please check your credentials.";
      toast.error(message);
    }
  };

  return (
    <main className="mx-auto w-full max-w-5xl px-4 py-12">
      <section className="card-modern grid min-h-[580px] md:grid-cols-[2.5fr_2.75fr] overflow-hidden">
        {/* Left: Image (larger, elongated) */}
        <div className="relative bg-muted/20">
          <img
            src={cameraPic}
            alt="Vintage camera in grass"
            className="h-full w-full object-cover"
          />
        </div>

        {/* Right: Form */}
        <div className="flex flex-col justify-center gap-8 p-8 md:p-12">
          <div className="space-y-1">
            <h1 className="text-3xl md:text-4xl font-semibold tracking-tight">
              Welcome back
            </h1>
            <p className="text-sm text-foreground/70">
              Please enter your details to continue
            </p>
          </div>

          <form className="flex flex-col gap-5" onSubmit={handleSubmit}>
            <div className="flex flex-col gap-2">
              <label htmlFor="userEmail" className="text-sm font-medium">
                Email
              </label>
              <Input
                id="userEmail"
                name="userEmail"
                type="email"
                placeholder="Enter your email"
                autoComplete="email"
                className="h-11"
                value={userEmail}
                onChange={(e) => setUserEmail(e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="password" className="text-sm font-medium">
                Password
              </label>
              <div className="relative">
                <Input
                  id="password"
                  name="password"
                  type={show ? "text" : "password"}
                  placeholder="••••••••"
                  autoComplete="current-password"
                  className="h-11 pr-11"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <button
                  type="button"
                  aria-label={show ? "Hide password" : "Show password"}
                  onClick={() => setShow((s) => !s)}
                  className="absolute inset-y-0 right-0 my-auto mr-2 inline-flex size-9 items-center justify-center rounded-md hover:bg-muted transition-colors"
                >
                  {show ? (
                    <EyeOff className="size-4" />
                  ) : (
                    <Eye className="size-4" />
                  )}
                </button>
              </div>
            </div>

            <Button
              type="submit"
              disabled={isLoading}
              className="mt-2 h-11 rounded-lg bg-foreground text-background hover:bg-foreground/90 disabled:opacity-50"
            >
              {isLoading ? "Signing in..." : "Sign in"}
            </Button>

            <p className="text-sm text-foreground/70 text-center">
              Don’t have an account?{" "}
              <a href="/register" className="font-medium hover:underline">
                Sign up
              </a>
            </p>
          </form>
        </div>
      </section>
    </main>
  );
}
