import React, { useState } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { userApi } from "../api/user.api";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim()) return;

    setIsLoading(true);
    setError(null);

    try {
      await userApi.forgotPassword(email.trim());
      setIsSubmitted(true);
    } catch (err: any) {
      console.error("Forgot password request failed:", err);
      // For security and privacy, if email not found or general error, show appropriate message
      if (err.response?.status === 404) {
        setIsSubmitted(true);
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to process request. Please try again later."
        );
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-80px)] w-full flex items-center justify-center p-4 sm:p-8 bg-background transition-colors duration-200">
      {/* Center Elongated Forgot Password Card */}
      <div className="w-full max-w-[400px] sm:max-w-[440px] min-h-[480px] sm:min-h-[520px] rounded-2xl border border-border bg-card p-8 sm:p-12 flex flex-col justify-between shadow-sm transition-all duration-200">
        
        {/* Top Header - Slid Upward on Y-axis */}
        <div className="text-center space-y-2.5 pt-1 sm:pt-2">
          <h1 className="text-xl sm:text-[22px] font-bold tracking-wider text-foreground uppercase">
            {isSubmitted ? "Check Your Inbox" : "Forgot Password"}
          </h1>
          <p className="text-xs sm:text-[13px] leading-relaxed text-muted-foreground">
            {isSubmitted
              ? `If an account exists for ${email}, a password reset link has been sent.`
              : "Enter your email to receive a password reset link."}
          </p>
        </div>

        {/* Form or Submitted State - Separated from Header */}
        <div className="my-auto py-6 sm:py-8">
          {error && (
            <div className="mb-5 p-3 rounded-lg bg-destructive/10 text-destructive text-xs text-center font-medium">
              {error}
            </div>
          )}

          {!isSubmitted ? (
            <form onSubmit={handleSubmit} className="space-y-5">
              <div className="space-y-2">
                <label
                  htmlFor="email"
                  className="block text-[11px] font-semibold tracking-wider text-muted-foreground uppercase"
                >
                  Email Address
                </label>
                <input
                  id="email"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="Enter your email..."
                  required
                  disabled={isLoading}
                  className="w-full h-12 rounded-lg border border-border bg-background px-3.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:outline-none focus:ring-1 focus:ring-foreground transition-all disabled:opacity-50"
                />
              </div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full h-12 rounded-lg bg-black text-white hover:bg-neutral-800 dark:bg-white dark:text-black dark:hover:bg-neutral-200 font-medium text-sm transition-colors cursor-pointer disabled:opacity-50 flex items-center justify-center"
              >
                {isLoading ? "Sending..." : "Send Reset Link"}
              </button>
            </form>
          ) : (
            <div className="space-y-3">
              <button
                type="button"
                onClick={() => {
                  setIsSubmitted(false);
                  setEmail("");
                }}
                className="w-full h-12 rounded-lg bg-black text-white hover:bg-neutral-800 dark:bg-white dark:text-black dark:hover:bg-neutral-200 font-medium text-sm transition-colors cursor-pointer flex items-center justify-center"
              >
                Send Another Link
              </button>
            </div>
          )}
        </div>

        {/* Return to Sign In Link - Anchored at Bottom */}
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
