import { useEffect, useState } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import { ordersApi } from "../api/orders.api";
import type { OrderResponse } from "../types";
import toast from "react-hot-toast";
import { AlertTriangle, Loader2 } from "lucide-react";

export default function OrderConfirmation() {
  const { orderId } = useParams();
  const navigate = useNavigate();

  const [order, setOrder] = useState<OrderResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isCancelling, setIsCancelling] = useState(false);
  const [showCancelModal, setShowCancelModal] = useState(false);

  const handleCancelOrder = async () => {
    if (!orderId) return;
    try {
      setIsCancelling(true);
      await ordersApi.cancelOrder(Number(orderId));
      toast.success("Order cancelled successfully");
      setShowCancelModal(false);
      const updatedOrder = await ordersApi.getOrderById(Number(orderId));
      setOrder(updatedOrder);
    } catch (err: any) {
      toast.error(err?.response?.data?.errorMessage || "Failed to cancel order");
    } finally {
      setIsCancelling(false);
    }
  };

  const address = order?.addressSnapshot || null;

  useEffect(() => {
    let isMounted = true;
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const fetchedOrder = await ordersApi.getOrderById(Number(orderId));
        if (!isMounted) return;
        setOrder(fetchedOrder);
        // If still CREATED right after checkout, retry once after 1.5s to catch webhook update
        if (fetchedOrder.orderStatus === "CREATED") {
          setTimeout(async () => {
            if (!isMounted) return;
            try {
              const refreshed = await ordersApi.getOrderById(Number(orderId));
              if (isMounted) setOrder(refreshed);
            } catch {}
          }, 1500);
        }
      } catch (err) {
        console.error("Failed to fetch order details", err);
        toast.error("Order not found");
        navigate("/orders");
      } finally {
        if (isMounted) setIsLoading(false);
      }
    };
    fetchData();
    return () => {
      isMounted = false;
    };
  }, [orderId, navigate]);


  if (isLoading) {
    return (
      <div className="min-h-screen bg-background text-foreground flex items-center justify-center pt-24 pb-20">
        <div className="animate-pulse space-y-4 text-center">
          <div className="h-8 w-64 bg-muted/30 rounded mx-auto"></div>
          <div className="h-4 w-48 bg-muted/30 rounded mx-auto"></div>
        </div>
      </div>
    );
  }

  if (!order) return null;

  const subtotal = order.items.reduce(
    (sum, item) => sum + item.unitPrice * item.quantity,
    0,
  );
  const shipping = 9.0; // Hardcoded matching checkout
  const tax = 5.0; // Hardcoded matching checkout
  const total = subtotal + shipping + tax;
  const isPaid = order.orderStatus !== "CREATED"; // Assuming CREATED means unpaid

  return (
    <div className="min-h-screen bg-background text-foreground font-sans pt-24 pb-20 px-4 md:px-8 transition-colors duration-200">
      {/* Page Header */}
      <div className="max-w-5xl mx-auto mb-10">
        <nav className="font-mono text-[10px] uppercase tracking-widest opacity-60 mb-4">
          <Link to="/orders" className="hover:opacity-100 transition-opacity">
            ORDERS
          </Link>{" "}
          / DETAILS
        </nav>
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
          <div>
            <h1 className="text-4xl font-bold tracking-tight uppercase mb-2">
              ORDER #
              {order.orderReference
                ? order.orderReference.toUpperCase()
                : order.orderId}
            </h1>
            <p className="text-sm text-muted-foreground font-medium uppercase tracking-widest">
              Placed on{" "}
              {new Date(order.placedAt || Date.now()).toLocaleDateString(
                "en-US",
                { month: "long", day: "numeric", year: "numeric" },
              )}
            </p>
          </div>
          <div className="flex items-center gap-3">
            <div
              className={`px-4 py-1.5 rounded-full text-xs font-bold tracking-widest border ${order.orderStatus === "CANCELLED" ? "border-destructive text-destructive" : "border-emerald-500/50 text-emerald-500"}`}
            >
              {order.orderStatus === "CANCELLED"
                ? "CANCELLED"
                : order.orderStatus === "CREATED"
                  ? "PENDING PAYMENT"
                  : order.orderStatus}
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto flex flex-col lg:flex-row gap-8">
        {/* Left Column: Order Items & Shipping */}
        <div className="flex-1 space-y-8">
          <div className="bg-card border border-border rounded-xl p-6 shadow-sm">
            <h2 className="text-xs font-bold tracking-widest uppercase mb-6 text-muted-foreground">
              Order Items
            </h2>
            <div className="space-y-6">
              {order.items.map((item, idx) => (
                <div key={idx} className="flex gap-4 sm:gap-6 items-center">
                  <div className="w-20 h-20 sm:w-24 sm:h-24 rounded-lg bg-secondary border border-border overflow-hidden shrink-0">
                    {item.primaryImageUrl ? (
                      <img
                        src={item.primaryImageUrl}
                        alt={item.productName || "Product"}
                        className="w-full h-full object-cover mix-blend-multiply dark:mix-blend-normal"
                      />
                    ) : (
                      <div className="w-full h-full bg-muted flex items-center justify-center text-[10px] text-muted-foreground">
                        IMG
                      </div>
                    )}
                  </div>
                  <div className="flex-1">
                    <h3 className="font-bold text-sm sm:text-base uppercase mb-1 line-clamp-1">
                      {item.productName}
                    </h3>
                    <p className="text-xs sm:text-sm text-muted-foreground mb-2">
                      Qty: {item.quantity}
                    </p>
                    <p className="font-bold tracking-tight text-sm sm:text-base">
                      $
                      {item.unitPrice.toLocaleString(undefined, {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2,
                      })}
                    </p>
                  </div>
                  <div className="font-extrabold tracking-tight text-right hidden sm:block">
                    $
                    {(item.unitPrice * item.quantity).toLocaleString(
                      undefined,
                      { minimumFractionDigits: 2, maximumFractionDigits: 2 },
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="bg-card border border-border rounded-xl p-6 shadow-sm">
            <h2 className="text-xs font-bold tracking-widest uppercase mb-6 text-muted-foreground">
              Shipping Destination
            </h2>
            {address ? (
              <div className="space-y-1">
                <div className="font-bold text-lg uppercase mb-2">
                  {address.fullName}
                </div>
                <div className="text-sm text-muted-foreground uppercase">
                  {address.flatHouse}
                </div>
                <div className="text-sm text-muted-foreground uppercase">
                  {address.area !== "N/A" ? address.area : ""}
                </div>
                <div className="text-sm text-muted-foreground uppercase">
                  {address.city}, {address.pincode}
                </div>
                <div className="text-sm text-muted-foreground uppercase">
                  {address.country}
                </div>
                <div className="text-sm text-muted-foreground uppercase mt-2">
                  Ph: {address.mobileNumber}
                </div>
              </div>
            ) : (
              <div className="text-sm text-muted-foreground">
                No shipping address recorded.
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Order Summary */}
        <div className="w-full lg:w-[380px] shrink-0">
          <div className="bg-card border border-border rounded-xl p-6 md:p-8 shadow-sm sticky top-24">
            <h2 className="text-xs font-bold tracking-widest uppercase mb-8 text-foreground">
              Order Summary
            </h2>

            <div className="space-y-4 mb-6 text-sm font-medium">
              <div className="flex justify-between items-center pb-4 border-b border-border/50">
                <span className="text-muted-foreground">Subtotal</span>
                <span>
                  $
                  {subtotal.toLocaleString(undefined, {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </span>
              </div>
              <div className="flex justify-between items-center pb-4 border-b border-border/50">
                <span className="text-muted-foreground">Shipping</span>
                <span>
                  $
                  {shipping.toLocaleString(undefined, {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </span>
              </div>
              <div className="flex justify-between items-center pb-4">
                <span className="text-muted-foreground">Tax</span>
                <span>
                  $
                  {tax.toLocaleString(undefined, {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </span>
              </div>
            </div>

            <div className="border-t-[2px] border-foreground pt-6 mb-8">
              <div className="flex justify-between items-end">
                <span className="text-sm font-bold tracking-widest uppercase text-foreground">
                  Total
                </span>
                <span className="text-3xl font-extrabold tracking-tight">
                  $
                  {total.toLocaleString(undefined, {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </span>
              </div>
            </div>

            {order.orderStatus === "CANCELLED" ? (
              <div className="w-full bg-destructive/10 text-destructive border border-destructive/20 py-3.5 rounded-lg font-bold text-xs tracking-widest uppercase flex justify-center items-center">
                ORDER CANCELLED
              </div>
            ) : !isPaid ? (
              <div className="space-y-3">
                <div className="w-full bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20 py-3 rounded-lg font-bold text-xs tracking-widest uppercase flex justify-center items-center">
                  PENDING PAYMENT
                </div>
                <button
                  type="button"
                  onClick={() => setShowCancelModal(true)}
                  disabled={isCancelling}
                  className="w-full py-3 rounded-lg font-bold text-xs tracking-widest uppercase text-destructive border border-destructive/30 hover:bg-destructive/10 transition-colors flex justify-center items-center"
                >
                  Cancel Order
                </button>
              </div>
            ) : (
              <div className="w-full bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 py-3.5 rounded-lg font-bold text-xs tracking-widest uppercase flex justify-center items-center gap-2">
                <span>PAYMENT COMPLETED</span>
                <span>✓</span>
              </div>
            )}

            <div className="mt-6 text-center">
              <Link
                to="/contact"
                className="text-[10px] font-bold tracking-widest uppercase text-muted-foreground hover:text-foreground hover:underline"
              >
                Need Help with this order?
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* Cancel Order Confirmation Modal */}
      {showCancelModal && order && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="bg-card border border-border rounded-xl p-6 max-w-md w-full shadow-xl">
            <div className="flex items-center gap-3 text-destructive mb-4">
              <div className="p-2 rounded-full bg-destructive/10">
                <AlertTriangle className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-foreground">Cancel Order</h3>
            </div>
            <p className="text-sm text-muted-foreground leading-relaxed mb-6">
              Are you sure you want to cancel order{" "}
              <strong className="text-foreground">
                #{order.orderReference ? order.orderReference.toUpperCase() : order.orderId}
              </strong>
              ? This will release reserved inventory and cancel this order.
            </p>
            <div className="flex items-center justify-end gap-3">
              <button
                type="button"
                onClick={() => setShowCancelModal(false)}
                disabled={isCancelling}
                className="px-4 py-2 text-sm font-medium border border-border rounded-lg hover:bg-muted text-foreground transition-colors disabled:opacity-50"
              >
                Keep Order
              </button>
              <button
                type="button"
                onClick={handleCancelOrder}
                disabled={isCancelling}
                className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium bg-destructive text-destructive-foreground rounded-lg hover:bg-destructive/90 transition-colors disabled:opacity-50"
              >
                {isCancelling && <Loader2 className="w-4 h-4 animate-spin" />}
                Confirm Cancellation
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
