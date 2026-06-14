import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams, Link } from "react-router-dom";
import { ordersApi } from "../api/orders.api";
import { addressesApi } from "../api/addresses.api";
import type { OrderResponse, Address } from "../types";
import toast from "react-hot-toast";

export default function OrderConfirmation() {
  const { orderId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  const [order, setOrder] = useState<OrderResponse | null>(location.state?.order || null);
  const [address, setAddress] = useState<Address | null>(null);
  const [isLoading, setIsLoading] = useState(!location.state?.order);
  const [isProcessingPayment, setIsProcessingPayment] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch order if not in state
        if (!order && orderId) {
          const fetchedOrder = await ordersApi.getOrderById(Number(orderId));
          setOrder(fetchedOrder);
        }

        // Fetch address for display
        const addresses = await addressesApi.getAddresses();
        if (addresses && addresses.length > 0) {
          setAddress(addresses[0]);
        }
      } catch (err) {
        console.error("Failed to fetch order/address details", err);
        toast.error("Failed to load order details.");
        navigate("/orders");
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [orderId, navigate]);

  const handleAuthorizePayment = async () => {
    if (!orderId) return;
    try {
      setIsProcessingPayment(true);
      const paymentInfo = await ordersApi.initiatePayment(Number(orderId));
      
      const loadScript = () => new Promise((resolve) => {
        const script = document.createElement("script");
        script.src = "https://checkout.razorpay.com/v1/checkout.js";
        script.onload = () => resolve(true);
        script.onerror = () => resolve(false);
        document.body.appendChild(script);
      });

      const isLoaded = await loadScript();
      if (!isLoaded) {
        toast.error("Failed to load Razorpay SDK. Please check your connection.");
        return;
      }

      const options = {
        key: import.meta.env.VITE_API_RAZORPAY_KEY_ID,
        amount: paymentInfo.amount,
        currency: paymentInfo.currency,
        name: "THE ARCHIVE",
        description: "Order Payment",
        order_id: paymentInfo.orderId,
        handler: function () {
          toast.success("Payment Successful!");
          // Reload the page to get the updated status
          window.location.reload();
        },
        prefill: {
          name: address?.fullName || "",
          contact: address?.mobileNumber || "",
        },
        theme: {
          color: "#ffffff",
        },
      };

      const rzp = new (window as any).Razorpay(options);
      
      rzp.on("payment.failed", function (response: any) {
        toast.error(`Payment Failed: ${response.error.description}`);
      });

      rzp.open();

    } catch (error: unknown) {
      const err = error as any;
      toast.error(err?.response?.data?.errorMessage || "Failed to initiate payment");
    } finally {
      setIsProcessingPayment(false);
    }
  };

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

  const subtotal = order.items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);
  const shipping = 9.00; // Hardcoded matching checkout
  const tax = 5.00; // Hardcoded matching checkout
  const total = subtotal + shipping + tax;
  const isPaid = order.orderStatus !== "CREATED" && order.orderStatus !== "PENDING"; // Assuming CREATED/PENDING means unpaid

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
              ORDER #{order.orderReference ? order.orderReference.toUpperCase() : order.orderId}
            </h1>
            <p className="text-sm text-muted-foreground font-medium uppercase tracking-widest">
              Placed on {new Date(order.placedAt || Date.now()).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })}
            </p>
          </div>
          <div className="flex items-center gap-3">
             <div className={`px-4 py-1.5 rounded-full text-xs font-bold tracking-widest border ${order.orderStatus === 'CANCELLED' ? 'border-destructive text-destructive' : 'border-emerald-500/50 text-emerald-500'}`}>
               {order.orderStatus === 'CANCELLED' ? 'CANCELLED' : order.orderStatus === 'CREATED' ? 'PENDING PAYMENT' : order.orderStatus}
             </div>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto flex flex-col lg:flex-row gap-8">
        
        {/* Left Column: Order Items & Shipping */}
        <div className="flex-1 space-y-8">
          
          <div className="bg-card border border-border rounded-xl p-6 shadow-sm">
            <h2 className="text-xs font-bold tracking-widest uppercase mb-6 text-muted-foreground">Order Items</h2>
            <div className="space-y-6">
              {order.items.map((item, idx) => (
                <div key={idx} className="flex gap-4 sm:gap-6 items-center">
                  <div className="w-20 h-20 sm:w-24 sm:h-24 rounded-lg bg-secondary border border-border overflow-hidden shrink-0">
                     <img 
                       src={item.primaryImageUrl || "https://placehold.co/100x100?text=IMG"} 
                       alt={item.productName || "Product"} 
                       className="w-full h-full object-cover mix-blend-multiply dark:mix-blend-normal"
                     />
                  </div>
                  <div className="flex-1">
                    <h3 className="font-bold text-sm sm:text-base uppercase mb-1 line-clamp-1">{item.productName}</h3>
                    <p className="text-xs sm:text-sm text-muted-foreground mb-2">Qty: {item.quantity}</p>
                    <p className="font-bold tracking-tight text-sm sm:text-base">
                      ${item.unitPrice.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                    </p>
                  </div>
                  <div className="font-extrabold tracking-tight text-right hidden sm:block">
                     ${(item.unitPrice * item.quantity).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="bg-card border border-border rounded-xl p-6 shadow-sm">
            <h2 className="text-xs font-bold tracking-widest uppercase mb-6 text-muted-foreground">Shipping Destination</h2>
            {address ? (
              <div className="space-y-1">
                <div className="font-bold text-lg uppercase mb-2">{address.fullName}</div>
                <div className="text-sm text-muted-foreground uppercase">{address.flatHouse}</div>
                <div className="text-sm text-muted-foreground uppercase">{address.area !== "N/A" ? address.area : ""}</div>
                <div className="text-sm text-muted-foreground uppercase">{address.city}, {address.pincode}</div>
                <div className="text-sm text-muted-foreground uppercase">{address.country}</div>
                <div className="text-sm text-muted-foreground uppercase mt-2">Ph: {address.mobileNumber}</div>
              </div>
            ) : (
              <div className="text-sm text-muted-foreground">No shipping address recorded.</div>
            )}
          </div>
        </div>

        {/* Right Column: Order Summary */}
        <div className="w-full lg:w-[380px] shrink-0">
          <div className="bg-card border border-border rounded-xl p-6 md:p-8 shadow-sm sticky top-24">
            <h2 className="text-xs font-bold tracking-widest uppercase mb-8 text-foreground">Order Summary</h2>
            
            <div className="space-y-4 mb-6 text-sm font-medium">
              <div className="flex justify-between items-center pb-4 border-b border-border/50">
                <span className="text-muted-foreground">Subtotal</span>
                <span>${subtotal.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
              <div className="flex justify-between items-center pb-4 border-b border-border/50">
                <span className="text-muted-foreground">Shipping</span>
                <span>${shipping.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
              <div className="flex justify-between items-center pb-4">
                <span className="text-muted-foreground">Tax</span>
                <span>${tax.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
            </div>

            <div className="border-t-[2px] border-foreground pt-6 mb-8">
              <div className="flex justify-between items-end">
                <span className="text-sm font-bold tracking-widest uppercase text-foreground">Total</span>
                <span className="text-3xl font-extrabold tracking-tight">
                  ${total.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </span>
              </div>
            </div>

            {!isPaid && order.orderStatus !== 'CANCELLED' ? (
              <button 
                onClick={handleAuthorizePayment}
                disabled={isProcessingPayment}
                className="w-full bg-foreground text-background py-4 rounded-lg font-bold text-xs tracking-widest uppercase hover:opacity-90 transition-opacity flex justify-center items-center"
              >
                {isProcessingPayment ? "PROCESSING..." : "AUTHORIZE PAYMENT →"}
              </button>
            ) : (
              <div className="w-full bg-muted text-muted-foreground py-4 rounded-lg font-bold text-xs tracking-widest uppercase flex justify-center items-center cursor-not-allowed">
                {order.orderStatus === 'CANCELLED' ? "ORDER CANCELLED" : "PAYMENT COMPLETED ✓"}
              </div>
            )}
            
            <div className="mt-6 text-center">
              <Link to="/contact" className="text-[10px] font-bold tracking-widest uppercase text-muted-foreground hover:text-foreground hover:underline">
                Need Help with this order?
              </Link>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
