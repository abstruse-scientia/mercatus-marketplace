import { useEffect } from "react";
import { Link } from "react-router-dom";
import { useCartStore } from "../store/cartStore";
import { Minus, Plus, Trash2, ArrowRight } from "lucide-react";
import { Button } from "./ui/button";
import { Card, CardContent } from "./ui/card";

export default function Cart() {
  const {
    cart,
    isLoading,
    error,
    fetchCart,
    updateQuantity,
    removeItem,
    clearCart,
    getItemCount,
  } = useCartStore();

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  if (isLoading && !cart) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-5xl">
        <h1 className="text-3xl font-bold mb-8">Shopping Cart</h1>
        <div className="grid md:grid-cols-3 gap-8">
          <div className="md:col-span-2 space-y-4">
            {[1, 2].map((i) => (
              <Card key={i} className="animate-pulse">
                <CardContent className="p-4 flex gap-4 h-32 bg-muted/50 rounded-lg"></CardContent>
              </Card>
            ))}
          </div>
          <div className="md:col-span-1">
            <Card className="animate-pulse h-48 bg-muted/50 rounded-lg"></Card>
          </div>
        </div>
      </div>
    );
  }

  const items = cart?.items || [];
  const itemCount = getItemCount();

  if (items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-16 text-center max-w-2xl">
        <h1 className="text-3xl font-bold mb-4">Your Cart is Empty</h1>
        <p className="text-muted-foreground mb-8">
          Looks like you haven't added anything to your cart yet.
        </p>
        <Button asChild size="lg">
          <Link to="/products">Continue Shopping</Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-6xl">
      <h1 className="text-3xl font-bold mb-8">
        Shopping Cart ({itemCount} {itemCount === 1 ? "item" : "items"})
      </h1>

      {error && (
        <div className="bg-destructive/10 text-destructive p-4 rounded-md mb-6">
          {error}
        </div>
      )}

      <div className="grid md:grid-cols-3 gap-8">
        <div className="md:col-span-2 space-y-4">
          <div className="flex justify-end mb-4">
            <Button
              variant="outline"
              size="sm"
              onClick={() => clearCart()}
              disabled={isLoading}
            >
              Clear Cart
            </Button>
          </div>
          {items.map((item) => (
            <Card key={item.id} className="overflow-hidden">
              <CardContent className="p-0">
                <div className="flex flex-col sm:flex-row p-4 gap-4 items-center sm:items-start whitespace-nowrap overflow-x-auto sm:whitespace-normal">
                  <div className="flex-1 min-w-0 text-center sm:text-left w-full">
                    <h3 className="font-semibold text-lg truncate">
                      {item.name}
                    </h3>
                    <p className="text-muted-foreground font-medium mt-1">
                      ${item.unitPrice.toFixed(2)}
                    </p>
                  </div>

                  <div className="flex items-center gap-4 sm:ml-auto w-full sm:w-auto justify-between sm:justify-end">
                    <div className="flex items-center border rounded-md h-9">
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-full w-9 rounded-r-none border-0"
                        onClick={() =>
                          updateQuantity(item.id, item.quantity - 1)
                        }
                        disabled={item.quantity <= 1 || isLoading}
                      >
                        <Minus className="h-3 w-3" />
                      </Button>
                      <div className="w-12 text-center text-sm font-medium">
                        {item.quantity}
                      </div>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-full w-9 rounded-l-none border-0"
                        onClick={() =>
                          updateQuantity(item.id, item.quantity + 1)
                        }
                        disabled={isLoading}
                      >
                        <Plus className="h-3 w-3" />
                      </Button>
                    </div>

                    <div className="font-semibold min-w-[80px] text-right text-lg">
                      ${item.totalItemsPrice.toFixed(2)}
                    </div>

                    <Button
                      variant="ghost"
                      size="icon"
                      className="text-muted-foreground hover:text-destructive shrink-0"
                      onClick={() => removeItem(item.id)}
                      disabled={isLoading}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <div className="md:col-span-1">
          <Card className="sticky top-20">
            <CardContent className="p-6">
              <h2 className="text-xl font-semibold mb-4">Order Summary</h2>

              <div className="space-y-3 mb-6">
                <div className="flex justify-between text-muted-foreground">
                  <span>Subtotal ({itemCount} items)</span>
                  <span>${cart?.subtotal?.toFixed(2) || "0.00"}</span>
                </div>
                <div className="flex justify-between text-muted-foreground">
                  <span>Shipping</span>
                  <span>Calculated at checkout</span>
                </div>
                <div className="border-t pt-3 flex justify-between font-semibold text-lg">
                  <span>Total</span>
                  <span>${cart?.subtotal?.toFixed(2) || "0.00"}</span>
                </div>
              </div>

              <Button className="w-full text-lg" size="lg" asChild>
                <Link to="/checkout">
                  Proceed to Checkout
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Link>
              </Button>

              <div className="mt-4 text-center">
                <Link
                  to="/products"
                  className="text-sm text-primary hover:underline"
                >
                  Continue Shopping
                </Link>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
