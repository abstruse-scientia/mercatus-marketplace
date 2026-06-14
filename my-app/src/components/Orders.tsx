import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ordersApi } from "../api/orders.api";
import type { OrderSummary } from "../types";
import toast from "react-hot-toast";
import { ChevronDown, ChevronUp, Package, CheckCircle, Truck, XCircle } from "lucide-react";

export default function Orders() {
  const [orders, setOrders] = useState<OrderSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [activeTab, setActiveTab] = useState<"ALL" | "CONFIRMED" | "CANCELLED">("ALL");
  const [expandedOrderId, setExpandedOrderId] = useState<number | null>(null);

  const fetchOrders = async (pageNumber: number, status: string) => {
    setIsLoading(true);
    try {
      const statusParam = status === "ALL" ? undefined : status;
      const response = await ordersApi.getOrders({ page: pageNumber, size: 10, status: statusParam });
      setOrders(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      toast.error("Failed to load orders history.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders(page, activeTab);
  }, [page, activeTab]);

  const handleTabChange = (tab: "ALL" | "CONFIRMED" | "CANCELLED") => {
    setActiveTab(tab);
    setPage(0);
    setExpandedOrderId(null);
  };

  const renderBadge = (order: OrderSummary) => {
    const isCancelled = order.orderStatus === 'CANCELLED';
    if (isCancelled) {
      return (
        <div className="flex items-center gap-2 px-3 py-1 bg-background text-destructive rounded-full text-[11px] font-bold tracking-widest border border-border">
          <span className="w-1.5 h-1.5 rounded-full bg-destructive"></span>
          CANCELLED
        </div>
      );
    }
    return (
      <div className="flex items-center gap-2 px-3 py-1 bg-background text-emerald-600 dark:text-emerald-400 rounded-full text-[11px] font-bold tracking-widest border border-border">
        <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
        {order.orderStatus === 'SHIPPED' ? 'DISPATCHED' : order.orderStatus === 'DELIVERED' ? 'COMPLETED' : order.orderStatus}
      </div>
    );
  };

  // Mocking summary stats since we don't have a summary endpoint yet.
  // In a real scenario, this would come from the backend.
  const confirmedCount = activeTab === "CONFIRMED" ? totalElements : (orders.filter(o => o.orderStatus !== 'CANCELLED').length);
  const cancelledCount = activeTab === "CANCELLED" ? totalElements : (orders.filter(o => o.orderStatus === 'CANCELLED').length);
  const lifetimeTotal = orders.reduce((sum, o) => sum + o.totalAmount, 0); // Mock for current page

  return (
    <div className="min-h-screen bg-background text-foreground font-sans pt-24 pb-20 px-4 md:px-8 transition-colors duration-200">
      
      {/* Page Header */}
      <div className="max-w-7xl mx-auto mb-8">
        <h1 className="text-3xl md:text-4xl font-bold tracking-tight uppercase mb-8">
          ORDER HISTORY
        </h1>

        {/* Tabs */}
        <div className="flex gap-8 border-b border-border/40 text-[11px] md:text-xs font-bold tracking-widest uppercase overflow-x-auto">
          <button 
            onClick={() => handleTabChange("ALL")}
            className={`pb-3 border-b-2 whitespace-nowrap transition-colors ${activeTab === "ALL" ? "border-foreground text-foreground" : "border-transparent text-muted-foreground hover:text-foreground"}`}
          >
            ALL ORDERS
          </button>
          <button 
            onClick={() => handleTabChange("CONFIRMED")}
            className={`pb-3 border-b-2 whitespace-nowrap transition-colors ${activeTab === "CONFIRMED" ? "border-foreground text-foreground" : "border-transparent text-muted-foreground hover:text-foreground"}`}
          >
            CONFIRMED
          </button>
          <button 
            onClick={() => handleTabChange("CANCELLED")}
            className={`pb-3 border-b-2 whitespace-nowrap transition-colors ${activeTab === "CANCELLED" ? "border-foreground text-foreground" : "border-transparent text-muted-foreground hover:text-foreground"}`}
          >
            CANCELLED
          </button>
        </div>
      </div>

      <div className="max-w-7xl mx-auto flex flex-col lg:flex-row gap-6 lg:gap-8">
        
        {/* Left Column: Orders Accordion */}
        <div className="flex-1">
          <div className="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
            {isLoading ? (
               <div className="p-6 space-y-6">
                  {[...Array(3)].map((_, i) => (
                    <div key={i} className="animate-pulse h-20 bg-muted/30 rounded-xl"></div>
                  ))}
               </div>
            ) : orders.length === 0 ? (
               <div className="text-center py-20">
                 <p className="text-muted-foreground font-medium mb-4">No orders found.</p>
                 <Link to="/products" className="text-sm font-bold uppercase tracking-widest hover:underline">Start Shopping →</Link>
               </div>
            ) : (
              <div className="flex flex-col">
                {orders.map((order) => {
                  const isExpanded = expandedOrderId === order.id;
                  const realItems = order.orderSummaryList || [];

                  return (
                    <div key={order.id} className="border-b border-border last:border-b-0">
                      {/* Accordion Header */}
                      <div 
                        onClick={() => setExpandedOrderId(isExpanded ? null : order.id)}
                        className="p-5 md:p-6 flex flex-col sm:flex-row justify-between sm:items-center gap-4 cursor-pointer hover:bg-muted/10 transition-colors"
                      >
                        <div className="flex flex-col sm:flex-row sm:items-center gap-4 md:gap-8">
                          <span className="text-sm font-bold uppercase">ORDER #{order.orderReference.toUpperCase()}</span>
                          <span className="text-sm text-muted-foreground font-medium uppercase">{new Date(order.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}</span>
                          {renderBadge(order)}
                        </div>
                        <div className="flex items-center justify-between sm:justify-end gap-4">
                          <span className="text-lg md:text-xl font-extrabold tracking-tight">
                            ${order.totalAmount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                          </span>
                          {isExpanded ? <ChevronUp className="w-5 h-5 text-muted-foreground" /> : <ChevronDown className="w-5 h-5 text-muted-foreground" />}
                        </div>
                      </div>

                      {/* Accordion Content */}
                      {isExpanded && (
                        <div className="px-5 md:px-6 pb-6 pt-2 border-t border-border/40 bg-muted/5 flex flex-col md:flex-row gap-8">
                          
                          {/* Left: Thumbnails */}
                          <div className="flex-1">
                            <div className="flex flex-wrap gap-2 mb-6">
                              {realItems.slice(0, 4).map((item, idx) => (
                                <div key={idx} className="w-20 h-20 md:w-24 md:h-24 rounded-xl border border-border bg-secondary flex items-center justify-center p-1 overflow-hidden">
                                    <img 
                                      src={item.primaryImageUrl || "https://placehold.co/100x100?text=IMG"} 
                                      alt={item.productName || "Product"} 
                                      className="w-full h-full object-cover mix-blend-multiply dark:mix-blend-normal rounded-lg"
                                    />
                                </div>
                              ))}
                              {realItems.length > 4 && (
                                <div className="w-20 h-20 md:w-24 md:h-24 rounded-xl border border-border bg-background flex flex-col items-center justify-center text-foreground hover:bg-secondary transition-colors cursor-pointer">
                                  <span className="font-bold text-lg">+{realItems.length - 4}</span>
                                  <span className="text-[10px] uppercase tracking-wider text-muted-foreground font-semibold mt-0.5">more</span>
                                </div>
                              )}
                              {realItems.length === 0 && (
                                <div className="w-20 h-20 md:w-24 md:h-24 rounded-xl border border-border border-dashed bg-background flex items-center justify-center text-xs text-muted-foreground text-center p-1">
                                  No images
                                </div>
                              )}
                            </div>
                          </div>

                          {/* Right: Details */}
                          <div className="flex-1 space-y-4 text-sm md:text-base">
                            <div className="grid grid-cols-3 gap-4 border-b border-border/40 pb-4">
                              <span className="text-muted-foreground text-[10px] md:text-xs font-bold tracking-widest uppercase">Status</span>
                              <span className="col-span-2 font-medium flex items-center gap-2">
                                {order.orderStatus === 'CANCELLED' ? 'Cancelled' : 'Confirmed'}
                                <span className={`w-1.5 h-1.5 rounded-full ${order.orderStatus === 'CANCELLED' ? 'bg-destructive' : 'bg-emerald-500'}`}></span>
                              </span>
                            </div>
                            <div className="grid grid-cols-3 gap-4 border-b border-border/40 pb-4">
                              <span className="text-muted-foreground text-[10px] md:text-xs font-bold tracking-widest uppercase">Payment Method</span>
                              <span className="col-span-2 font-medium">Standard Payment</span>
                            </div>
                            <div className="grid grid-cols-3 gap-4 pb-6">
                              <span className="text-muted-foreground text-[10px] md:text-xs font-bold tracking-widest uppercase">Shipping Address</span>
                              <span className="col-span-2 font-medium text-sm text-muted-foreground">
                                Address details available on order page.
                              </span>
                            </div>
                            
                            <Link 
                              to={`/orders/${order.id}`} 
                              className="inline-block bg-foreground text-background px-6 py-3 rounded-lg font-bold text-xs tracking-widest uppercase hover:opacity-90 transition-opacity"
                            >
                              View Order Details
                            </Link>
                          </div>

                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Pagination Controls */}
          <div className="mt-6 flex justify-between items-center text-sm">
            <span className="text-muted-foreground font-medium">
              Showing page {page + 1} of {totalPages}
            </span>
            <div className="flex gap-4 items-center">
              <button 
                disabled={page === 0}
                onClick={() => setPage(p => Math.max(0, p - 1))}
                className="text-muted-foreground hover:text-foreground disabled:opacity-30 disabled:cursor-not-allowed transition-colors font-mono"
              >
                ←
              </button>
              <div className="flex gap-4 font-mono font-bold">
                {Array.from({ length: totalPages }).map((_, i) => (
                  <button
                    key={i}
                    onClick={() => setPage(i)}
                    className={`transition-colors ${page === i ? "text-foreground border-b-2 border-foreground" : "text-muted-foreground hover:text-foreground"}`}
                  >
                    {i + 1}
                  </button>
                ))}
              </div>
              <button 
                disabled={page >= totalPages - 1}
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                className="text-muted-foreground hover:text-foreground disabled:opacity-30 disabled:cursor-not-allowed transition-colors font-mono"
              >
                →
              </button>
            </div>
          </div>
        </div>

        {/* Right Column: Sidebar */}
        <div className="w-full lg:w-[320px] shrink-0 space-y-6">
          
          {/* Order Summary Box */}
          <div className="bg-card border border-border rounded-xl p-6 shadow-sm">
            <h2 className="text-xs font-bold tracking-widest uppercase mb-6 text-foreground">Order Summary</h2>
            
            <div className="space-y-4 mb-6">
              <div className="flex justify-between items-center text-sm border-b border-border/40 pb-3">
                <div className="flex items-center gap-3 text-muted-foreground font-medium">
                  <Package className="w-4 h-4" />
                  <span>Total Orders</span>
                </div>
                <span className="font-bold text-foreground">{totalElements}</span>
              </div>
              <div className="flex justify-between items-center text-sm border-b border-border/40 pb-3">
                <div className="flex items-center gap-3 text-muted-foreground font-medium">
                  <CheckCircle className="w-4 h-4" />
                  <span>Confirmed</span>
                </div>
                <span className="font-bold text-foreground">{confirmedCount}</span>
              </div>
              <div className="flex justify-between items-center text-sm border-b border-border/40 pb-3">
                <div className="flex items-center gap-3 text-muted-foreground font-medium">
                  <Truck className="w-4 h-4" />
                  <span>Delivered</span>
                </div>
                <span className="font-bold text-foreground">0</span>
              </div>
              <div className="flex justify-between items-center text-sm pb-3">
                <div className="flex items-center gap-3 text-muted-foreground font-medium">
                  <XCircle className="w-4 h-4" />
                  <span>Cancelled</span>
                </div>
                <span className="font-bold text-foreground">{cancelledCount}</span>
              </div>
            </div>

            <div className="border-t border-border pt-4 mb-6">
              <div className="text-[10px] font-bold tracking-widest uppercase mb-1 text-muted-foreground">Lifetime Spend</div>
              <div className="text-2xl font-extrabold tracking-tight text-foreground">
                ${lifetimeTotal.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </div>
            </div>

            <Link to="/products" className="w-full flex justify-center items-center bg-foreground text-background py-3 rounded-lg font-bold text-xs tracking-widest uppercase hover:opacity-90 transition-opacity">
              Browse Collection →
            </Link>
          </div>

          {/* Need Help Box */}
          <div className="bg-card border border-border rounded-xl p-6 shadow-sm">
            <h2 className="text-xs font-bold tracking-widest uppercase mb-3 text-foreground">Need Help?</h2>
            <p className="text-sm text-muted-foreground mb-6">
              If you have any questions about your order, our support team is here to help.
            </p>
            <Link to="/contact" className="text-xs font-bold tracking-widest uppercase text-foreground hover:underline flex items-center gap-2">
              Contact Support →
            </Link>
          </div>

        </div>

      </div>
    </div>
  );
}
