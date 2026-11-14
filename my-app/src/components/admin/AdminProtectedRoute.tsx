
import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";

const AdminProtectedRoute = () => {
  const user = useAuthStore((state) => state.user);
  const location = useLocation();
  
  if (!user) {
    return <Navigate to="/login" replace  state = {{from: location }}/>;
  }

  // Check if user has ROLE_ADMIN or ADMIN (handles both string[] and string)
  const isAdmin = Array.isArray(user.roles)
    ? user.roles.includes("ROLE_ADMIN") || user.roles.includes("ADMIN")
    : typeof user.roles === "string" &&
      ((user.roles as string).includes("ROLE_ADMIN") || (user.roles as string).includes("ADMIN"));

  if (!isAdmin) {
    // If logged in but not admin, kick them to home
    return <Navigate to="/home" replace />;
  }

  return <Outlet />;
};

export default AdminProtectedRoute;
