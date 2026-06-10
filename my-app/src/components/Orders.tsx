import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ordersApi } from "../api/orders.api";
import type { OrderSummary } from "../types";
import toast from "react-hot-toast";

export default function Orders() {
  const [orders, setOrders] = useState<OrderSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await ordersApi.getOrders({ page: 0, size: 50 });
        setOrders(response.content);
      } catch (error) {
        toast.error("Failed to load orders history.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchOrders();
  }, []);

  return (
    <div className="min-h-screen bg-[#F4F4F4] font-mono text-black p-4 md:p-8 selection:bg-black selection:text-white pb-24">
      {/* Top Banner */}
      <div className="max-w-5xl mx-auto border-2 border-black bg-white mb-8">
        <div className="flex justify-between items-center p-4 border-b-2 border-black font-bold tracking-widest text-sm md:text-base">
          <div>[📦 MERCATUS]</div>
          <div className="hidden md:flex gap-8 text-xs">
            <Link to="/products" className="hover:underline">COLLECTION</Link>
            <Link to="/products" className="hover:underline">NEW ARRIVALS</Link>
          </div>
          <div className="flex gap-4">
            <span>🔍</span>
            <span>👤</span>
            <Link to="/cart">🛒</Link>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto border-2 border-black bg-white relative">
        {/* Breadcrumbs */}
        <div className="p-4 border-b-2 border-black text-xs md:text-sm font-bold tracking-widest text-gray-500 uppercase">
          <Link to="/home" className="hover:text-black">Home</Link> / <span className="text-black">Order Ledger</span>
        </div>

        {/* Title */}
        <div className="p-8 border-b-2 border-black flex justify-between items-end">
          <div>
            <h1 className="text-2xl md:text-4xl font-bold tracking-[0.2em] uppercase">Order Ledger</h1>
            <p className="mt-2 text-sm font-bold text-gray-500 uppercase">ARCHIVE OF PAST REQUISITIONS</p>
          </div>
          <div className="text-xs font-bold tracking-widest">
            [{orders.length} RECORDS FOUND]
          </div>
        </div>

        {/* Ledger Content */}
        <div className="p-8">
          {isLoading ? (
            <div className="animate-pulse text-sm font-bold uppercase tracking-widest">
              [ ACCESSING ARCHIVE... ]
            </div>
          ) : orders.length === 0 ? (
            <div className="text-sm font-bold uppercase tracking-widest text-gray-500 py-12 text-center border-2 border-dashed border-gray-300">
              NO RECORDS FOUND IN LEDGER
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm font-bold uppercase tracking-widest">
                <thead className="border-b-2 border-black">
                  <tr>
                    <th className="pb-4 pr-4">REFERENCE</th>
                    <th className="pb-4 pr-4">DATE</th>
                    <th className="pb-4 pr-4">AMOUNT</th>
                    <th className="pb-4 pr-4">PAYMENT</th>
                    <th className="pb-4 text-right">STATUS</th>
                  </tr>
                </thead>
                <tbody className="divide-y-2 divide-black divide-dashed">
                  {orders.map((order) => (
                    <tr key={order.id} className="hover:bg-[#F4F4F4] transition-colors">
                      <td className="py-4 pr-4 truncate max-w-[120px] md:max-w-none" title={order.orderReference}>
                        {order.orderReference.substring(0, 8)}...
                      </td>
                      <td className="py-4 pr-4">
                        {new Date(order.createdAt).toLocaleDateString()}
                      </td>
                      <td className="py-4 pr-4">
                        ${order.totalAmount.toFixed(2)}
                      </td>
                      <td className="py-4 pr-4">
                        <span className={`px-2 py-1 border-2 ${order.orderPaymentStatus === 'PAID' ? 'border-black bg-black text-white' : 'border-gray-400 text-gray-500'}`}>
                          [{order.orderPaymentStatus}]
                        </span>
                      </td>
                      <td className="py-4 text-right">
                        {order.orderStatus}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Decorative corner accents */}
        <div className="absolute top-0 left-0 w-4 h-4 border-b-2 border-r-2 border-white bg-black"></div>
        <div className="absolute top-0 right-0 w-4 h-4 border-b-2 border-l-2 border-white bg-black"></div>
        <div className="absolute bottom-0 left-0 w-4 h-4 border-t-2 border-r-2 border-white bg-black"></div>
        <div className="absolute bottom-0 right-0 w-4 h-4 border-t-2 border-l-2 border-white bg-black"></div>
      </div>
    </div>
  );
}
