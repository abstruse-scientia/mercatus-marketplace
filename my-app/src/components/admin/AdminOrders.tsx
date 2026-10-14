import React, { useState, useEffect } from "react";
import { Loader2 } from "lucide-react";
import { adminApi } from "../../api/admin.api";
import type { AdminOrderSummary, OrderStatus } from "../../types";
import toast from "react-hot-toast";

const orderStatusColors: Record<OrderStatus, string> = {
  CREATED: "bg-blue-50 text-blue-700 border-blue-200 dark:bg-blue-500/10 dark:text-blue-400 dark:border-blue-500/20",
  CONFIRMED: "bg-purple-50 text-purple-700 border-purple-200 dark:bg-purple-500/10 dark:text-purple-400 dark:border-purple-500/20",
  SHIPPED: "bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-500/10 dark:text-amber-400 dark:border-amber-500/20",
  DELIVERED: "bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/20",
  CANCELLED: "bg-red-50 text-red-700 border-red-200 dark:bg-red-500/10 dark:text-red-400 dark:border-red-500/20",
  RETURNED: "bg-orange-50 text-orange-700 border-orange-200 dark:bg-orange-500/10 dark:text-orange-400 dark:border-orange-500/20",
};

const AdminOrders: React.FC = () => {
  const [orders, setOrders] = useState<AdminOrderSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  
  // Filters and Pagination
  const [statusFilter, setStatusFilter] = useState<OrderStatus | "ALL">("ALL");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 15;

  const [updatingOrderId, setUpdatingOrderId] = useState<number | null>(null);

  const fetchOrders = async () => {
    try {
      setIsLoading(true);
      const statusParam = statusFilter === "ALL" ? undefined : statusFilter;
      const data = await adminApi.listAllOrders(page, pageSize, statusParam);
      setOrders(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (error) {
      toast.error("Failed to load orders");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [page, statusFilter]);

  const handleStatusChange = async (orderId: number, newStatus: OrderStatus) => {
    try {
      setUpdatingOrderId(orderId);
      await adminApi.updateOrderStatus(orderId, newStatus);
      toast.success("Order status updated");
      fetchOrders();
    } catch (error) {
      toast.error("Failed to update order status");
    } finally {
      setUpdatingOrderId(null);
    }
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-white">Orders</h1>
          <p className="text-sm text-neutral-600 dark:text-neutral-400 mt-1">
            Manage customer orders and fulfillments.
          </p>
        </div>
      </div>

      <div className="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl overflow-hidden shadow-sm">
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-800 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-2 overflow-x-auto pb-2 sm:pb-0 hide-scrollbar">
            {["ALL", "CREATED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED", "RETURNED"].map((status) => (
              <button
                key={status}
                onClick={() => { setStatusFilter(status as any); setPage(0); }}
                className={`px-3 py-1.5 text-xs font-medium rounded-full whitespace-nowrap transition-colors ${
                  statusFilter === status 
                    ? "bg-black text-white dark:bg-white dark:text-black" 
                    : "bg-neutral-100 text-neutral-600 hover:bg-neutral-200 dark:bg-neutral-800 dark:text-neutral-400 dark:hover:bg-neutral-700"
                }`}
              >
                {status}
              </button>
            ))}
          </div>
          <div className="text-sm text-neutral-500 shrink-0">
            Total: {totalElements} orders
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-neutral-50 dark:bg-neutral-900/50 border-b border-neutral-200 dark:border-neutral-800">
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Order ID</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Customer</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Date</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Total</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Payment</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider text-right">Status Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-200 dark:divide-neutral-800">
              {isLoading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-neutral-500">
                    Loading orders...
                  </td>
                </tr>
              ) : orders.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-neutral-500">
                    No orders found.
                  </td>
                </tr>
              ) : (
                orders.map((order) => (
                  <tr key={order.id} className="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="text-sm font-medium text-neutral-900 dark:text-white font-mono">
                        {order.orderReference}
                      </div>
                      <div className="text-xs text-neutral-500">
                        #{order.id}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="text-sm font-medium text-neutral-900 dark:text-white">
                        {order.userName}
                      </div>
                      <div className="text-xs text-neutral-500">
                        {order.userEmail}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-neutral-600 dark:text-neutral-400">
                      {new Date(order.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-sm font-medium text-neutral-900 dark:text-white">
                      ${order.totalAmount.toFixed(2)}
                    </td>
                    <td className="px-6 py-4">
                      <span className={`inline-flex items-center px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider border ${
                        order.orderPaymentStatus === 'SUCCESS' ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-500/10 dark:text-emerald-400' :
                        order.orderPaymentStatus === 'PENDING' ? 'bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-500/10 dark:text-amber-400' :
                        'bg-red-50 text-red-700 border-red-200 dark:bg-red-500/10 dark:text-red-400'
                      }`}>
                        {order.orderPaymentStatus}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      {updatingOrderId === order.id ? (
                        <div className="flex items-center justify-end">
                          <Loader2 className="w-5 h-5 animate-spin text-neutral-400" />
                        </div>
                      ) : (
                        <select
                          value={order.orderStatus}
                          onChange={(e) => handleStatusChange(order.id, e.target.value as OrderStatus)}
                          className={`text-xs font-semibold uppercase tracking-wider rounded-md border px-2 py-1.5 focus:outline-none focus:ring-2 focus:ring-black dark:focus:ring-white appearance-none text-center cursor-pointer ${orderStatusColors[order.orderStatus]}`}
                        >
                          <option value="CREATED">Created</option>
                          <option value="CONFIRMED">Confirmed</option>
                          <option value="SHIPPED">Shipped</option>
                          <option value="DELIVERED">Delivered</option>
                          <option value="CANCELLED">Cancelled</option>
                          <option value="RETURNED">Returned</option>
                        </select>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="p-4 border-t border-neutral-200 dark:border-neutral-800 flex items-center justify-between">
            <span className="text-sm text-neutral-500">
              Showing page {page + 1} of {totalPages}
            </span>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-1.5 text-sm border border-neutral-300 dark:border-neutral-700 rounded-md hover:bg-neutral-50 dark:hover:bg-neutral-800 disabled:opacity-50 transition-colors"
              >
                Previous
              </button>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
                className="px-3 py-1.5 text-sm border border-neutral-300 dark:border-neutral-700 rounded-md hover:bg-neutral-50 dark:hover:bg-neutral-800 disabled:opacity-50 transition-colors"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminOrders;
