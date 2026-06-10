import { useEffect } from "react";
import { Link } from "react-router-dom";
import { useCartStore } from "../store/cartStore";
import { Minus, Plus, X } from "lucide-react";
import { Button } from "./ui/button";

export default function Cart() {
  const {
    cart,
    isLoading,
    error,
    fetchCart,
    updateQuantity,
    removeItem,
  } = useCartStore();

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  if (isLoading && !cart) {
    return (
      <div className="container mx-auto px-4 py-12 max-w-6xl">
        <h1 className="text-4xl font-bold mb-12 tracking-tight">Cart</h1>
        <div className="grid md:grid-cols-3 gap-12">
          <div className="md:col-span-2 space-y-8">
            {[1, 2].map((i) => (
              <div key={i} className="animate-pulse flex gap-6 pb-8 border-b">
                <div className="w-32 h-40 bg-muted rounded-md"></div>
                <div className="flex-1 space-y-4 py-2">
                  <div className="h-6 bg-muted rounded w-1/3"></div>
                  <div className="space-y-2">
                    <div className="h-4 bg-muted rounded w-1/4"></div>
                    <div className="h-4 bg-muted rounded w-1/5"></div>
                  </div>
                </div>
              </div>
            ))}
          </div>
          <div className="md:col-span-1">
            <div className="bg-muted/30 rounded-2xl p-8 h-80 animate-pulse"></div>
          </div>
        </div>
      </div>
    );
  }

  const items = cart?.items || [];

  if (items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-24 text-center max-w-2xl">
        <h1 className="text-4xl font-bold mb-6 tracking-tight">Your Cart is Empty</h1>
        <p className="text-muted-foreground mb-10 text-lg">
          Looks like you haven't added anything to your cart yet.
        </p>
        <Button asChild size="lg" className="rounded-full px-8 py-6 text-base font-medium">
          <Link to="/products">Continue Shopping</Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-12 max-w-6xl">
      <h1 className="text-4xl font-bold mb-12 tracking-tight">Cart</h1>

      {error && (
        <div className="bg-destructive/10 text-destructive p-4 rounded-md mb-8 font-medium">
          {error}
        </div>
      )}

      <div className="grid lg:grid-cols-[1fr_400px] gap-12 xl:gap-20">
        {/* Left Column - Items */}
        <div className="bg-[#F8F9FA] dark:bg-muted/20 rounded-[20px] p-8 space-y-8">
          {items.map((item) => (
            <div key={item.id} className="flex gap-6 pb-8 border-b border-foreground/10 last:border-0 relative">
              {/* Product Image */}
              <div className="w-[140px] aspect-[4/5] bg-muted/50 rounded-lg overflow-hidden shrink-0">
                {item.primaryImageUrl ? (
                  <img
                    src={item.primaryImageUrl}
                    alt={item.name}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-muted-foreground text-xs uppercase tracking-widest">
                    No Image
                  </div>
                )}
              </div>

              {/* Product Details */}
              <div className="flex-1 flex justify-between items-start pt-1 pr-12">
                
                {/* Info (Left) */}
                <div className="space-y-4">
                  <h3 className="font-semibold text-[17px] leading-tight text-foreground">
                    <Link to={`/products/${item.productId}`} className="hover:underline">
                      {item.name}
                    </Link>
                  </h3>
                  <div className="space-y-1.5">
                    <p className="text-[14px] text-muted-foreground">
                      Category: <span className="text-foreground/80">{item.categoryName || "Standard"}</span>
                    </p>
                    <p className="text-[14px] text-muted-foreground">
                      Size: <span className="text-foreground/80">Standard</span>
                    </p>
                  </div>
                </div>
                
                {/* Price & Quantity (Right) */}
                <div className="flex flex-col items-center gap-5 mt-0.5">
                  <span className="font-semibold text-[17px]">
                    ${item.unitPrice.toFixed(2)}
                  </span>
                  <div className="flex items-center gap-4">
                    <button
                      onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                      disabled={item.quantity <= 1 || isLoading}
                      className="w-7 h-7 flex items-center justify-center border border-foreground/20 rounded-full hover:border-foreground transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
                    >
                      <Minus className="w-3.5 h-3.5" />
                    </button>
                    <span className="w-5 text-center font-medium text-[15px]">{item.quantity}</span>
                    <button
                      onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                      disabled={isLoading}
                      className="w-7 h-7 flex items-center justify-center border border-foreground/20 rounded-full hover:border-foreground transition-colors disabled:opacity-30"
                    >
                      <Plus className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

              </div>

              {/* Delete Button (Absolute Top Right Corner) */}
              <button 
                onClick={() => removeItem(item.productId)}
                disabled={isLoading}
                className="absolute top-0 right-0 text-foreground/40 hover:text-foreground transition-colors disabled:opacity-50"
                aria-label="Remove item"
              >
                <X className="w-5 h-5 font-light" strokeWidth={1.5} />
              </button>
            </div>
          ))}
        </div>

        {/* Right Column - Order Summary */}
        <div>
          <div className="bg-[#F8F9FA] dark:bg-muted/20 rounded-[20px] p-8 lg:sticky lg:top-24">
            <h2 className="text-[22px] font-semibold mb-8 tracking-tight">Order Summary</h2>

            <div className="space-y-4 mb-8">
              <div className="flex justify-between items-center text-[15px]">
                <span className="text-muted-foreground">Subtotal</span>
                <span className="font-medium">${cart?.subtotal?.toFixed(2) || "0.00"}</span>
              </div>
              <div className="flex justify-between items-center text-[15px]">
                <span className="text-muted-foreground">Delivery</span>
                <span className="font-medium">Calculated at checkout</span>
              </div>
              <div className="flex justify-between items-center text-[15px]">
                <span className="text-muted-foreground">Discount</span>
                <span className="font-medium">-</span>
              </div>
            </div>

            <div className="border-t border-foreground/10 pt-6 mb-8 flex justify-between items-center">
              <span className="text-lg font-semibold">Total</span>
              <span className="text-lg font-semibold">${cart?.subtotal?.toFixed(2) || "0.00"}</span>
            </div>

            <Button 
              className="w-full h-[52px] text-[15px] font-medium bg-black text-white hover:bg-black/90 dark:bg-white dark:text-black dark:hover:bg-white/90 rounded-xl" 
              asChild
            >
              <Link to="/checkout">Checkout</Link>
            </Button>

            <div className="mt-6">
              <button className="text-[13px] text-foreground underline underline-offset-4 hover:text-muted-foreground transition-colors">
                Use a promo code
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
