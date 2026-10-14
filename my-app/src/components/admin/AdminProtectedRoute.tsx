import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";

const AdminProtectedRoute: React.FC = () => {
  const user = useAuthStore((state: any) => state.user);
  
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Check if user has ROLE_ADMIN
  const isAdmin = user.roles.includes("ROLE_ADMIN");

  if (!isAdmin) {
    // If logged in but not admin, kick them to home
    return <Navigate to="/home" replace />;
  }

  return <Outlet />;
};

export default AdminProtectedRoute;
