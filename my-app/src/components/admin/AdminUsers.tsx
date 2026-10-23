import { useState, useEffect } from "react";
import {
  Loader2,
  Shield,
  ShieldAlert,
  Power,
  PowerOff,
  Trash2,
  AlertTriangle,
  User as UserIcon,
  Search,
} from "lucide-react";
import { adminApi } from "../../api/admin.api";
import { useAuthStore } from "../../store/authStore";
import type { AdminUserSummary } from "../../types";
import toast from "react-hot-toast";

const AdminUsers = () => {
  const [users, setUsers] = useState<AdminUserSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  // Search filter
  const [searchTerm, setSearchTerm] = useState("");

  // Action states
  const [actionLoadingId, setActionLoadingId] = useState<number | null>(null);
  const [userToDelete, setUserToDelete] = useState<AdminUserSummary | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const currentUser = useAuthStore((state) => state.user);

  const fetchUsers = async () => {
    try {
      setIsLoading(true);
      const data = await adminApi.listUsers(page, pageSize);
      setUsers(data.content || []);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);
    } catch (error: any) {
      toast.error(error.response?.data?.errorMessage || "Failed to load users");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page]);

  const hasAdminRole = (u: AdminUserSummary) => {
    return (u.roles || []).some(
      (r) => r.name === "ROLE_ADMIN" || r.name === "ADMIN"
    );
  };

  const handleToggleRole = async (targetUser: AdminUserSummary) => {
    const isAdmin = hasAdminRole(targetUser);
    const newRoles = isAdmin
      ? [{ name: "ROLE_USER" }]
      : [{ name: "ROLE_USER" }, { name: "ROLE_ADMIN" }];

    try {
      setActionLoadingId(targetUser.userId);
      await adminApi.updateUserRoles(targetUser.userId, newRoles);
      toast.success(
        isAdmin
          ? `Demoted ${targetUser.userName} to User`
          : `Promoted ${targetUser.userName} to Admin`
      );
      await fetchUsers();
    } catch (error: any) {
      toast.error(error.response?.data?.errorMessage || "Failed to update user roles");
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleToggleStatus = async (targetUser: AdminUserSummary) => {
    try {
      setActionLoadingId(targetUser.userId);
      if (targetUser.isActive) {
        await adminApi.deactivateUser(targetUser.userId);
        toast.success(`User ${targetUser.userName} deactivated`);
      } else {
        await adminApi.reactivateUser(targetUser.userId);
        toast.success(`User ${targetUser.userName} reactivated`);
      }
      await fetchUsers();
    } catch (error: any) {
      toast.error(error.response?.data?.errorMessage || "Failed to update user status");
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleDeleteUser = async () => {
    if (!userToDelete) return;
    try {
      setIsDeleting(true);
      await adminApi.deleteUser(userToDelete.userId);
      toast.success(`User ${userToDelete.userName} deleted`);
      setUserToDelete(null);
      await fetchUsers();
    } catch (error: any) {
      toast.error(error.response?.data?.errorMessage || "Failed to delete user");
    } finally {
      setIsDeleting(false);
    }
  };

  const filteredUsers = users.filter((u) => {
    if (!searchTerm.trim()) return true;
    const term = searchTerm.toLowerCase();
    return (
      u.userName?.toLowerCase().includes(term) ||
      u.email?.toLowerCase().includes(term)
    );
  });

  return (
    <div className="flex flex-col gap-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-white">Users</h1>
          <p className="text-sm text-neutral-600 dark:text-neutral-400 mt-1">
            Manage system users, access roles, and account statuses.
          </p>
        </div>
      </div>

      {/* Table Container */}
      <div className="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl overflow-hidden shadow-sm">
        {/* Table Toolbar */}
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="relative w-full sm:max-w-xs">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search by name or email..."
              className="w-full pl-9 pr-4 py-2 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-sm focus:ring-2 focus:ring-black dark:focus:ring-white focus:border-transparent outline-none text-neutral-900 dark:text-white placeholder-neutral-400"
            />
          </div>
          <div className="text-sm text-neutral-500">
            Total: {totalElements} users
          </div>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-neutral-50 dark:bg-neutral-900/50 border-b border-neutral-200 dark:border-neutral-800">
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">User</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Email</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Roles</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-200 dark:divide-neutral-800">
              {isLoading ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center text-neutral-500">
                    <div className="flex flex-col items-center justify-center gap-2">
                      <Loader2 className="w-6 h-6 animate-spin text-neutral-400" />
                      <span>Loading users...</span>
                    </div>
                  </td>
                </tr>
              ) : filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center text-neutral-500">
                    {searchTerm ? "No users match your search." : "No users found."}
                  </td>
                </tr>
              ) : (
                filteredUsers.map((user) => {
                  const isSelf = currentUser?.userId === user.userId;
                  const isAdmin = hasAdminRole(user);
                  const isActionLoading = actionLoadingId === user.userId;

                  return (
                    <tr
                      key={user.userId}
                      className="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors"
                    >
                      {/* User Info */}
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-9 h-9 rounded-full bg-neutral-100 dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 flex items-center justify-center shrink-0 font-semibold text-sm text-neutral-700 dark:text-neutral-300">
                            {user.userName ? user.userName.charAt(0).toUpperCase() : <UserIcon className="w-4 h-4" />}
                          </div>
                          <div>
                            <div className="flex items-center gap-2">
                              <span className="text-sm font-medium text-neutral-900 dark:text-white">
                                {user.userName}
                              </span>
                              {isSelf && (
                                <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-semibold bg-blue-50 text-blue-700 border border-blue-200 dark:bg-blue-500/10 dark:text-blue-400 dark:border-blue-500/20">
                                  You
                                </span>
                              )}
                            </div>
                            <span className="text-xs text-neutral-500 font-mono">
                              ID: #{user.userId}
                            </span>
                          </div>
                        </div>
                      </td>

                      {/* Email */}
                      <td className="px-6 py-4 text-sm text-neutral-600 dark:text-neutral-400 font-mono">
                        {user.email}
                      </td>

                      {/* Roles */}
                      <td className="px-6 py-4">
                        <div className="flex flex-wrap gap-1.5">
                          {user.roles && user.roles.length > 0 ? (
                            user.roles.map((r, idx) => {
                              const isRoleAdmin = r.name === "ROLE_ADMIN" || r.name === "ADMIN";
                              return (
                                <span
                                  key={idx}
                                  className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium border ${
                                    isRoleAdmin
                                      ? "bg-purple-50 text-purple-700 border-purple-200 dark:bg-purple-500/10 dark:text-purple-400 dark:border-purple-500/20"
                                      : "bg-neutral-100 text-neutral-700 border-neutral-200 dark:bg-neutral-800 dark:text-neutral-300 dark:border-neutral-700"
                                  }`}
                                >
                                  {isRoleAdmin ? <Shield className="w-3 h-3" /> : null}
                                  {isRoleAdmin ? "Admin" : "User"}
                                </span>
                              );
                            })
                          ) : (
                            <span className="text-xs text-neutral-400">None</span>
                          )}
                        </div>
                      </td>

                      {/* Status */}
                      <td className="px-6 py-4">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-[11px] font-semibold uppercase tracking-wider border ${
                            user.isActive
                              ? "bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/20"
                              : "bg-red-50 text-red-700 border-red-200 dark:bg-red-500/10 dark:text-red-400 dark:border-red-500/20"
                          }`}
                        >
                          {user.isActive ? "Active" : "Inactive"}
                        </span>
                      </td>

                      {/* Actions */}
                      <td className="px-6 py-4 text-right">
                        {isSelf ? (
                          <span className="text-xs text-neutral-400 italic">
                            Protected
                          </span>
                        ) : (
                          <div className="flex items-center justify-end gap-1.5">
                            {isActionLoading ? (
                              <Loader2 className="w-4 h-4 animate-spin text-neutral-400" />
                            ) : (
                              <>
                                {/* Promote / Demote */}
                                <button
                                  onClick={() => handleToggleRole(user)}
                                  className={`p-2 rounded-lg text-xs font-medium transition-colors border ${
                                    isAdmin
                                      ? "text-neutral-700 dark:text-neutral-300 border-neutral-300 dark:border-neutral-700 hover:bg-neutral-100 dark:hover:bg-neutral-800"
                                      : "text-purple-700 dark:text-purple-400 border-purple-200 dark:border-purple-500/30 hover:bg-purple-50 dark:hover:bg-purple-500/10"
                                  }`}
                                  title={isAdmin ? "Demote to User" : "Promote to Admin"}
                                >
                                  {isAdmin ? (
                                    <span className="flex items-center gap-1">
                                      <ShieldAlert className="w-3.5 h-3.5" />
                                      <span className="hidden lg:inline">Demote</span>
                                    </span>
                                  ) : (
                                    <span className="flex items-center gap-1">
                                      <Shield className="w-3.5 h-3.5" />
                                      <span className="hidden lg:inline">Make Admin</span>
                                    </span>
                                  )}
                                </button>

                                {/* Deactivate / Reactivate */}
                                <button
                                  onClick={() => handleToggleStatus(user)}
                                  className={`p-2 rounded-lg text-xs font-medium transition-colors border ${
                                    user.isActive
                                      ? "text-amber-700 dark:text-amber-400 border-amber-200 dark:border-amber-500/30 hover:bg-amber-50 dark:hover:bg-amber-500/10"
                                      : "text-emerald-700 dark:text-emerald-400 border-emerald-200 dark:border-emerald-500/30 hover:bg-emerald-50 dark:hover:bg-emerald-500/10"
                                  }`}
                                  title={user.isActive ? "Deactivate User" : "Reactivate User"}
                                >
                                  {user.isActive ? (
                                    <span className="flex items-center gap-1">
                                      <PowerOff className="w-3.5 h-3.5" />
                                      <span className="hidden lg:inline">Deactivate</span>
                                    </span>
                                  ) : (
                                    <span className="flex items-center gap-1">
                                      <Power className="w-3.5 h-3.5" />
                                      <span className="hidden lg:inline">Reactivate</span>
                                    </span>
                                  )}
                                </button>

                                {/* Delete */}
                                <button
                                  onClick={() => setUserToDelete(user)}
                                  className="p-2 text-red-600 dark:text-red-400 border border-red-200 dark:border-red-500/30 hover:bg-red-50 dark:hover:bg-red-500/10 rounded-lg text-xs font-medium transition-colors"
                                  title="Delete User"
                                >
                                  <span className="flex items-center gap-1">
                                    <Trash2 className="w-3.5 h-3.5" />
                                    <span className="hidden lg:inline">Delete</span>
                                  </span>
                                </button>
                              </>
                            )}
                          </div>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="p-4 border-t border-neutral-200 dark:border-neutral-800 flex items-center justify-between">
            <span className="text-sm text-neutral-500">
              Showing page {page + 1} of {totalPages}
            </span>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-1.5 text-sm border border-neutral-300 dark:border-neutral-700 rounded-md hover:bg-neutral-50 dark:hover:bg-neutral-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-neutral-700 dark:text-neutral-300"
              >
                Previous
              </button>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
                className="px-3 py-1.5 text-sm border border-neutral-300 dark:border-neutral-700 rounded-md hover:bg-neutral-50 dark:hover:bg-neutral-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-neutral-700 dark:text-neutral-300"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Delete Confirmation Modal */}
      {userToDelete && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl p-6 max-w-md w-full shadow-xl">
            <div className="flex items-center gap-3 text-red-600 dark:text-red-400 mb-4">
              <div className="p-2 rounded-full bg-red-100 dark:bg-red-500/10">
                <AlertTriangle className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-neutral-900 dark:text-white">Delete User</h3>
            </div>
            <p className="text-sm text-neutral-600 dark:text-neutral-400 leading-relaxed mb-6">
              Are you sure you want to permanently delete user{" "}
              <strong className="text-neutral-900 dark:text-white">
                {userToDelete.userName}
              </strong>{" "}
              ({userToDelete.email})? This action cannot be undone.
            </p>
            <div className="flex items-center justify-end gap-3">
              <button
                type="button"
                onClick={() => setUserToDelete(null)}
                disabled={isDeleting}
                className="px-4 py-2 text-sm font-medium border border-neutral-300 dark:border-neutral-700 rounded-lg hover:bg-neutral-100 dark:hover:bg-neutral-800 text-neutral-700 dark:text-neutral-300 transition-colors disabled:opacity-50"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleDeleteUser}
                disabled={isDeleting}
                className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
              >
                {isDeleting && <Loader2 className="w-4 h-4 animate-spin" />}
                Confirm Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminUsers;
