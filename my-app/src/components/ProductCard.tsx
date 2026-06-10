import { useState } from "react";
import { useCartStore } from "@/store/cartStore";
import type { Product } from "@/types";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";

interface PropductCardProps {
  product: Product;
}

export default function ProductCard({ product }: PropductCardProps) {
  const [isAdding, setIsAdding] = useState(false);
  const addItem = useCartStore((state) => state.addItem);
  const cartItems = useCartStore((state) => state.cart?.items) || [];

  const isInCart = cartItems.some(
    (item) => item.productId === product.id || item.id === product.id,
  );

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.preventDefault(); // Prevents the <Link> from navigating to the detail page
    try {
      setIsAdding(true);
      await addItem(product.id, 1);
      toast.success(`Added ${product.name} to cart`);
    } catch {
      toast.error("Failed to add to cart");
    } finally {
      setIsAdding(false);
    }
  };
  return (
    <Link to={`/products/${product.id}`} className="group block">
      <div className="relative aspect-[4/3] sm:aspect-square bg-muted overflow-hidden rounded-[2px]">
        <img
          src={product.primaryImageUrl}
          alt={product.name}
          className="object-cover w-full h-full transition-transform duration-[800ms] ease-[cubic-bezier(0.25,0.46,0.45,0.94)] group-hover:scale-105"
        />
        {/* Subtle overlay on hover for premium feel */}
        <div className="absolute inset-0 bg-black/0 transition-colors duration-300 group-hover:bg-black/5 dark:group-hover:bg-white/5" />
      </div>
      <div className="mt-[16px] flex flex-col">
        <h3 className="text-[14px] font-medium leading-[1.3] truncate">
          {product.name}
        </h3>
        <div className="text-[11px] font-medium tracking-[0.1em] uppercase text-foreground/50 mt-[4px] mb-[12px]">
          {product.categoryName}
        </div>

        <div className="flex items-center justify-between mt-auto">
          <span className="text-[15px] font-normal tracking-[-0.01em]">
            ${product.price.toFixed(2)}
          </span>
          <button
            onClick={handleAddToCart}
            disabled={isAdding || isInCart}
            className="text-[11px] font-semibold tracking-[0.06em] uppercase bg-black text-white px-4 py-2 hover:bg-black/80 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
          >
            {isAdding ? "Adding..." : isInCart ? "In Cart" : "Add to Cart"}
          </button>
        </div>
      </div>
    </Link>
  );
}
