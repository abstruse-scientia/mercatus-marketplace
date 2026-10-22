import React, { useState } from "react";
import { Link, useSearchParams, useNavigate } from "react-router-dom";
import { Eye, EyeOff, ArrowLeft, CheckCircle2, AlertCircle } from "lucide-react";
import { userApi } from "../api/user.api";

export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  // Read reset token from URL: supports ?token=... or ?accessToken=...
  const token = searchParams.get("token") || searchParams.get("accessToken") || "";

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!token) {
      setError("Reset token is missing or invalid. Please request a new link.");
      return;
    }

    if (newPassword.length < 8) {
      setError("Password must be at least 8 characters long.");
      return;
    }

    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      await userApi.resetPassword({
        accessToken: token,
        newPassword,
      });
      setIsSuccess(true);
    } catch (err: any) {
      console.error("Reset password failed:", err);
      const message =
        err.response?.data?.message ||
        "Unable to reset password. The link may have expired or is invalid.";
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-80px)] w-full flex items-center justify-center p-4 sm:p-8 bg-background transition-colors duration-200">
      {/* Center Elongated Reset Password Card */}
      <div className="w-full max-w-[400px] sm:max-w-[440px] min-h-[480px] sm:min-h-[520px] rounded-2xl border border-border bg-card p-8 sm:p-12 flex flex-col justify-between shadow-sm transition-all duration-200">
        
        {/* Top Header */}
        <div className="text-center space-y-2.5 pt-1 sm:pt-2">
          <h1 className="text-xl sm:text-[22px] font-bold tracking-wider text-foreground uppercase">
            {isSuccess
              ? "Password Reset Complete"
              : !token
              ? "Invalid Reset Link"
              : "Reset Password"}
          </h1>
          <p className="text-xs sm:text-[13px] leading-relaxed text-muted-foreground">
            {isSuccess
              ? "Your password has been successfully updated."
              : !token
              ? "This password reset link is invalid or has expired."
              : "Enter your new password below to update your credentials."}
          </p>
        </div>

        {/* Content Body */}
        <div className="my-auto py-6 sm:py-8">
          {/* Error Message */}
          {error && (
            <div className="mb-5 p-3 rounded-lg bg-destructive/10 text-destructive text-xs text-center font-medium flex items-center justify-center gap-1.5">
              <AlertCircle className="size-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Case 1: Missing / Invalid Token */}
          {!token ? (
            <div className="space-y-4 text-center">
              <p className="text-xs sm:text-sm text-muted-foreground">
                Please request a new password reset link to continue.
              </p>
              <Link
                to="/forgot-password"
                className="w-full h-12 rounded-lg bg-black text-white hover:bg-neutral-800 dark:bg-white dark:text-black dark:hover:bg-neutral-200 font-medium text-sm transition-colors cursor-pointer flex items-center justify-center"
              >
                Request New Link
              </Link>
            </div>
          ) : isSuccess ? (
            /* Case 2: Success State */
            <div className="space-y-5 text-center">
              <div className="mx-auto flex size-12 items-center justify-center rounded-full bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
                <CheckCircle2 className="size-6" />
              </div>
              <p className="text-xs sm:text-sm text-muted-foreground">
                You can now sign in with your new password.
              </p>
              <button
                type="button"
                onClick={() => navigate("/login")}
                className="w-full h-12 rounded-lg bg-black text-white hover:bg-neutral-800 dark:bg-white dark:text-black dark:hover:bg-neutral-200 font-medium text-sm transition-colors cursor-pointer flex items-center justify-center"
              >
                Proceed to Sign In
              </button>
            </div>
          ) : (
            /* Case 3: Reset Password Form */
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* New Password */}
              <div className="space-y-1.5">
                <label
                  htmlFor="newPassword"
                  className="block text-[11px] font-semibold tracking-wider text-muted-foreground uppercase"
                >
                  New Password
                </label>
                <div className="relative">
                  <input
                    id="newPassword"
                    type={showPassword ? "text" : "password"}
                    autoComplete="new-password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    placeholder="At least 8 characters"
                    required
                    disabled={isLoading}
                    className="w-full h-12 rounded-lg border border-border bg-background px-3.5 pr-11 text-sm text-foreground placeholder:text-muted-foreground/60 focus:outline-none focus:ring-1 focus:ring-foreground transition-all disabled:opacity-50"
                  />
                  <button
                    type="button"
                    aria-label={showPassword ? "Hide password" : "Show password"}
                    onClick={() => setShowPassword((prev) => !prev)}
                    className="absolute inset-y-0 right-0 my-auto mr-2 inline-flex size-9 items-center justify-center rounded-md text-muted-foreground hover:text-foreground transition-colors"
                  >
                    {showPassword ? (
                      <EyeOff className="size-4" />
                    ) : (
                      <Eye className="size-4" />
                    )}
                  </button>
                </div>
              </div>

              {/* Confirm New Password */}
              <div className="space-y-1.5">
                <label
                  htmlFor="confirmPassword"
                  className="block text-[11px] font-semibold tracking-wider text-muted-foreground uppercase"
                >
                  Confirm New Password
                </label>
                <div className="relative">
                  <input
                    id="confirmPassword"
                    type={showConfirmPassword ? "text" : "password"}
                    autoComplete="new-password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder="Re-enter new password"
                    required
                    disabled={isLoading}
                    className="w-full h-12 rounded-lg border border-border bg-background px-3.5 pr-11 text-sm text-foreground placeholder:text-muted-foreground/60 focus:outline-none focus:ring-1 focus:ring-foreground transition-all disabled:opacity-50"
                  />
                  <button
                    type="button"
                    aria-label={
                      showConfirmPassword
                        ? "Hide password"
                        : "Show password"
                    }
                    onClick={() => setShowConfirmPassword((prev) => !prev)}
                    className="absolute inset-y-0 right-0 my-auto mr-2 inline-flex size-9 items-center justify-center rounded-md text-muted-foreground hover:text-foreground transition-colors"
                  >
                    {showConfirmPassword ? (
                      <EyeOff className="size-4" />
                    ) : (
                      <Eye className="size-4" />
                    )}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full h-12 mt-2 rounded-lg bg-black text-white hover:bg-neutral-800 dark:bg-white dark:text-black dark:hover:bg-neutral-200 font-medium text-sm transition-colors cursor-pointer disabled:opacity-50 flex items-center justify-center"
              >
                {isLoading ? "Updating Password..." : "Reset Password"}
              </button>
            </form>
          )}
        </div>

        {/* Return to Sign In Link */}
        <div className="pt-2 text-center">
          <Link
            to="/login"
            className="inline-flex items-center gap-1.5 text-xs sm:text-[13px] text-muted-foreground hover:text-foreground transition-colors font-medium"
          >
            <ArrowLeft className="size-3.5" />
            <span>Return to Sign In</span>
          </Link>
        </div>
      </div>
    </div>
  );
}
