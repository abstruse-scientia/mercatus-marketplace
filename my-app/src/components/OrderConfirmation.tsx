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

  const [order] = useState<OrderResponse | null>(location.state?.order || null);
  const [address, setAddress] = useState<Address | null>(null);
  const [isProcessingPayment, setIsProcessingPayment] = useState(false);

  useEffect(() => {
    // If order isn't in state, we would ideally fetch it here.
    // Since backend lacks getOrderById right now, if no state, redirect to orders list.
    if (!order) {
      navigate("/orders");
      return;
    }

    // Fetch address for display
    const fetchAddr = async () => {
      try {
        const addresses = await addressesApi.getAddresses();
        if (addresses && addresses.length > 0) {
          setAddress(addresses[0]);
        }
      } catch (err) {
        console.error("Failed to fetch address", err);
      }
    };
    fetchAddr();
  }, [order, navigate]);

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
        key: import.meta.env.VITE_API_RAZORPAY_KEY_ID, // Use frontend env var
        amount: paymentInfo.amount,
        currency: paymentInfo.currency,
        name: "MERCATUS",
        description: "Requisition Payment",
        order_id: paymentInfo.orderId,
        handler: function () {
          toast.success("Uplink Established! Payment Captured.");
          // Ideally we would poll the backend or redirect to a success/orders page here
          navigate("/orders");
        },
        prefill: {
          name: address?.fullName || "",
          contact: address?.mobileNumber || "",
        },
        theme: {
          color: "#000000",
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

  if (!order) return null;

  const subtotal = order.items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);
  const shipping = 9.00; // Hardcoded matching checkout
  const tax = 5.00; // Hardcoded matching checkout
  const total = subtotal + shipping + tax;

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
            <span>🛒</span>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto border-2 border-black bg-white relative">
        {/* Breadcrumbs */}
        <div className="p-4 border-b-2 border-black text-xs md:text-sm font-bold tracking-widest text-gray-500 uppercase">
          <Link to="/home" className="hover:text-black">Home</Link> / <Link to="/cart" className="hover:text-black">Cart</Link> / <span className="text-black">Dispatch</span>
        </div>

        {/* Title */}
        <div className="p-8 border-b-2 border-black">
          <h1 className="text-2xl md:text-4xl font-bold tracking-[0.2em] uppercase">Dispatch Manifest</h1>
          <p className="mt-2 text-sm font-bold text-gray-500 uppercase">Order REF: #{orderId}</p>
        </div>

        {/* 2-Column Content */}
        <div className="flex flex-col md:flex-row">
          
          {/* Left Column: Destination */}
          <div className="flex-1 p-8 border-b-2 md:border-b-0 md:border-r-2 border-black">
            <h2 className="text-sm font-bold tracking-widest uppercase mb-8">1. Shipping Destination</h2>
            
            {address ? (
              <div className="space-y-1 mb-12">
                <div className="text-xs font-bold tracking-widest text-gray-500 mb-4">[ ACTIVE RECORD ]</div>
                <div className="font-bold text-lg uppercase">{address.fullName}</div>
                <div className="uppercase">{address.flatHouse}</div>
                <div className="uppercase">{address.area !== "N/A" ? address.area : ""}</div>
                <div className="uppercase">{address.city}, {address.pincode}</div>
                <div className="uppercase">{address.country}</div>
              </div>
            ) : (
              <div className="mb-12 animate-pulse">Loading address records...</div>
            )}

            <div className="flex gap-4 text-xs font-bold tracking-widest">
              <button className="border-2 border-black px-4 py-2 hover:bg-black hover:text-white transition-colors uppercase">
                [ Change ]
              </button>
              <button className="border-2 border-black px-4 py-2 hover:bg-black hover:text-white transition-colors uppercase text-gray-500">
                [ + New Address ]
              </button>
            </div>

            <div className="mt-12 pt-12 border-t-2 border-black border-dashed opacity-50 pointer-events-none">
              <div className="text-xs font-bold tracking-widest mb-6">&gt; NEW DESTINATION RECORD</div>
              <div className="grid grid-cols-2 gap-8 mb-6">
                <div>
                  <div className="text-xs font-bold mb-2">FIRST NAME</div>
                  <div className="border-b-2 border-black h-6"></div>
                </div>
                <div>
                  <div className="text-xs font-bold mb-2">LAST NAME</div>
                  <div className="border-b-2 border-black h-6"></div>
                </div>
              </div>
              <div className="mb-8">
                <div className="text-xs font-bold mb-2">STREET ADDRESS</div>
                <div className="border-b-2 border-black h-6"></div>
              </div>
              <button className="text-xs font-bold tracking-widest">[ SAVE TO LEDGER ]</button>
            </div>
          </div>

          {/* Right Column: Summary */}
          <div className="w-full md:w-[450px] p-8 flex flex-col">
            <h2 className="text-sm font-bold tracking-widest uppercase mb-8">Requisition Summary</h2>
            
            <div className="space-y-4 mb-12 flex-1">
              {order.items.map((item, idx) => (
                <div key={idx} className="flex justify-between text-sm md:text-base font-bold uppercase">
                  <span className="truncate pr-4">{item.quantity}X {item.productName}</span>
                  <span className="shrink-0">${item.unitPrice.toFixed(2)}</span>
                </div>
              ))}
            </div>

            <div className="space-y-2 mb-6 text-sm font-bold uppercase">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Shipping</span>
                <span>${shipping.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Tax</span>
                <span>${tax.toFixed(2)}</span>
              </div>
            </div>

            <div className="border-t-2 border-black pt-6 mb-8 flex justify-between items-center text-xl md:text-2xl font-bold uppercase">
              <span>Total</span>
              <span>${total.toFixed(2)}</span>
            </div>

            <button 
              onClick={handleAuthorizePayment}
              disabled={isProcessingPayment}
              className="w-full bg-black text-white py-6 text-sm font-bold tracking-widest uppercase hover:bg-gray-800 transition-colors flex justify-center items-center"
            >
              {isProcessingPayment ? "[ ESTABLISHING UPLINK... ]" : "[ AUTHORIZE PAYMENT ➔ ]"}
            </button>
          </div>
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
