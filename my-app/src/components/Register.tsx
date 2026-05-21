import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";
import toast from "react-hot-toast";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import cameraPic from "../assets/images/camera-pic.jpg";
import { useAuthStore } from "../store/authStore";
import type { AxiosError } from "axios";

export default function Register() {
  const navigate = useNavigate();
  const register = useAuthStore((state) => state.register);
  const isLoading = useAuthStore((state) => state.isLoading);

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [formData, setFormData] = useState({
    userName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (
      !formData.userName ||
      formData.userName.length < 2 ||
      formData.userName.length > 100
    ) {
      newErrors.userName = "Username must be between 2 and 100 characters.";
    }
    if (!formData.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Please enter a valid email address.";
    }
    if (
      !formData.password ||
      !/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}/.test(formData.password)
    ) {
      newErrors.password =
        "Password must be at least 8 characters long and contain uppercase, lowercase, number, and special character.";
    }
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match.";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Clear error for field on change
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) {
      toast.error("Please fix the validation errors.");
      return;
    }

    try {
      await register(formData);
      toast.success("Successfully registered!");
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
        {/* Left: Image */}
        <div className="relative bg-muted/20">
          <img
            src={cameraPic}
            alt="Vintage camera"
            className="h-full w-full object-cover"
          />
        </div>

        {/* Right: Form */}
        <div className="flex flex-col justify-center gap-8 p-8 md:p-12">
          <div className="space-y-1">
            <h1 className="text-3xl md:text-4xl font-semibold tracking-tight">
              Create an account
            </h1>
            <p className="text-sm text-foreground/70">
              Sign up to start shopping for vintage cameras
            </p>
          </div>

          <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
            <div className="flex flex-col gap-2">
              <label htmlFor="userName" className="text-sm font-medium">
                Username
              </label>
              <Input
                id="userName"
                name="userName"
                type="text"
                placeholder="Enter your username"
                autoComplete="username"
                className="h-11"
                value={formData.userName}
                onChange={handleChange}
              />
              {errors.userName && (
                <span className="text-xs text-red-500">{errors.userName}</span>
              )}
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="email" className="text-sm font-medium">
                Email
              </label>
              <Input
                id="email"
                name="email"
                type="email"
                placeholder="Enter your email"
                autoComplete="email"
                className="h-11"
                value={formData.email}
                onChange={handleChange}
              />
              {errors.email && (
                <span className="text-xs text-red-500">{errors.email}</span>
              )}
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="password" className="text-sm font-medium">
                Password
              </label>
              <div className="relative">
                <Input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="••••••••"
                  autoComplete="new-password"
                  className="h-11 pr-11"
                  value={formData.password}
                  onChange={handleChange}
                />
                <button
                  type="button"
                  aria-label={showPassword ? "Hide password" : "Show password"}
                  onClick={() => setShowPassword((s) => !s)}
                  className="absolute inset-y-0 right-0 my-auto mr-2 inline-flex size-9 items-center justify-center rounded-md hover:bg-muted transition-colors"
                >
                  {showPassword ? (
                    <EyeOff className="size-4" />
                  ) : (
                    <Eye className="size-4" />
                  )}
                </button>
              </div>
              {errors.password && (
                <span className="text-xs text-red-500">{errors.password}</span>
              )}
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="confirmPassword" className="text-sm font-medium">
                Confirm Password
              </label>
              <div className="relative">
                <Input
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  placeholder="••••••••"
                  autoComplete="new-password"
                  className="h-11 pr-11"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                />
                <button
                  type="button"
                  aria-label={
                    showConfirmPassword ? "Hide password" : "Show password"
                  }
                  onClick={() => setShowConfirmPassword((s) => !s)}
                  className="absolute inset-y-0 right-0 my-auto mr-2 inline-flex size-9 items-center justify-center rounded-md hover:bg-muted transition-colors"
                >
                  {showConfirmPassword ? (
                    <EyeOff className="size-4" />
                  ) : (
                    <Eye className="size-4" />
                  )}
                </button>
              </div>
              {errors.confirmPassword && (
                <span className="text-xs text-red-500">
                  {errors.confirmPassword}
                </span>
              )}
            </div>

            <Button
              type="submit"
              disabled={isLoading}
              className="mt-2 h-11 rounded-lg bg-foreground text-background hover:bg-foreground/90 disabled:opacity-50"
            >
              {isLoading ? "Signing up..." : "Sign up"}
            </Button>

            <p className="text-sm text-foreground/70 text-center">
              Already have an account?{" "}
              <a href="/login" className="font-medium hover:underline">
                Sign in
              </a>
            </p>
          </form>
        </div>
      </section>
    </main>
  );
}
