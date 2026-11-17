import React, { useState } from "react";
import { User, KeyRound, Eye, EyeOff, Mail } from "lucide-react";
import toast from "react-hot-toast";
import { useAuthStore } from "../store/authStore";
import { userApi } from "../api/user.api";

const UserProfile = () => {
  const user = useAuthStore((state) => state.user);

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!newPassword) {
      toast.error("Please enter a new password");
      return;
    }

    if (newPassword.length < 8) {
      toast.error("Password must be at least 8 characters long");
      return;
    }

    if (newPassword !== confirmPassword) {
      toast.error("New passwords do not match");
      return;
    }

    try {
      setIsUpdating(true);
      await userApi.changePassword(newPassword);
      toast.success("Password updated successfully!");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err: any) {
      console.error("Change password error:", err);
      const message =
        err.response?.data?.message ||
        "Failed to update password. Please try again.";
      toast.error(message);
    } finally {
      setIsUpdating(false);
    }
  };

  const initial = user?.username ? user.username.charAt(0).toUpperCase() : "U";

  return (
    <div className="space-y-10">
      {/* Top Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">
          Profile Settings
        </h1>
        <p className="text-sm text-muted-foreground mt-1">
          View your personal information and update your security credentials.
        </p>
      </div>

      {/* Section A: Profile Information */}
      <section className="border border-border rounded-xl p-6 sm:p-8 bg-muted/20">
        <div className="flex flex-col sm:flex-row items-start sm:items-center gap-6 pb-6 border-b border-border">
          {/* Avatar / User Initial */}
          <div className="size-16 rounded-full bg-muted flex items-center justify-center text-2xl font-bold text-foreground border border-border shrink-0">
            {initial}
          </div>

          <div className="space-y-1 flex-1">
            <h2 className="text-lg font-semibold text-foreground">
              {user?.username || "Valued Customer"}
            </h2>
            <p className="text-sm text-muted-foreground flex items-center gap-1.5">
              <Mail className="size-3.5" />
              <span>{user?.email || "No email on file"}</span>
            </p>
          </div>
        </div>

        {/* Read-Only Details Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 pt-6">
          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              Username
            </label>
            <div className="h-11 flex items-center px-3.5 rounded-lg border border-border bg-card text-sm text-foreground font-medium">
              <User className="size-4 text-muted-foreground mr-2 shrink-0" />
              {user?.username || "—"}
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              Email Address
            </label>
            <div className="h-11 flex items-center px-3.5 rounded-lg border border-border bg-card text-sm text-foreground font-medium">
              <Mail className="size-4 text-muted-foreground mr-2 shrink-0" />
              {user?.email || "—"}
            </div>
          </div>
        </div>
      </section>

      {/* Section B: Security / Change Password */}
      <section className="border border-border rounded-xl p-6 sm:p-8 bg-muted/20">
        <div className="flex items-center gap-2 mb-2">
          <KeyRound className="size-5 text-foreground" />
          <h2 className="text-lg font-semibold text-foreground">
            Security & Password
          </h2>
        </div>
        <p className="text-sm text-muted-foreground mb-6">
          Ensure your account is using a long, random password to stay secure.
        </p>

        <form onSubmit={handleChangePassword} className="space-y-5 max-w-lg">
          {/* New Password */}
          <div className="space-y-1.5">
            <label
              htmlFor="newPassword"
              className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground"
            >
              New Password
            </label>
            <div className="relative">
              <input
                id="newPassword"
                type={showNewPassword ? "text" : "password"}
                autoComplete="new-password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="At least 8 characters"
                disabled={isUpdating}
                className="w-full h-11 rounded-lg border border-border bg-background px-3.5 pr-11 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring transition-all disabled:opacity-50"
              />
              <button
                type="button"
                aria-label={
                  showNewPassword ? "Hide password" : "Show password"
                }
                onClick={() => setShowNewPassword((prev) => !prev)}
                className="absolute inset-y-0 right-0 my-auto mr-2 inline-flex size-8 items-center justify-center rounded-md text-muted-foreground hover:text-foreground transition-colors"
              >
                {showNewPassword ? (
                  <EyeOff className="size-4" />
                ) : (
                  <Eye className="size-4" />
                )}
              </button>
            </div>
          </div>

          {/* Confirm Password */}
          <div className="space-y-1.5">
            <label
              htmlFor="confirmPassword"
              className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground"
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
                disabled={isUpdating}
                className="w-full h-11 rounded-lg border border-border bg-background px-3.5 pr-11 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring transition-all disabled:opacity-50"
              />
              <button
                type="button"
                aria-label={
                  showConfirmPassword ? "Hide password" : "Show password"
                }
                onClick={() => setShowConfirmPassword((prev) => !prev)}
                className="absolute inset-y-0 right-0 my-auto mr-2 inline-flex size-8 items-center justify-center rounded-md text-muted-foreground hover:text-foreground transition-colors"
              >
                {showConfirmPassword ? (
                  <EyeOff className="size-4" />
                ) : (
                  <Eye className="size-4" />
                )}
              </button>
            </div>
          </div>

          <div className="pt-2">
            <button
              type="submit"
              disabled={isUpdating || !newPassword}
              className="inline-flex items-center justify-center px-6 py-2.5 rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 text-sm font-medium transition-colors disabled:opacity-50 cursor-pointer"
            >
              {isUpdating ? "Updating..." : "Update Password"}
            </button>
          </div>
        </form>
      </section>
    </div>
  );
};

export default UserProfile;