import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { productsApi } from "../api/products.api";
import type { Product } from "../types";
import { useCartStore } from "@/store/cartStore";
import toast from "react-hot-toast";

export default function ProductDetail() {
  const { id } = useParams<{ id: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [quantity, setQuantity] = useState(1);

  const [isAdding, setIsAdding] = useState(false);
  const addItem = useCartStore((state) => state.addItem);

  useEffect(() => {
    let isMounted = true;
    const fetchProduct = async () => {
      try {
        setLoading(true);
        if (!id) throw new Error("No product ID provided");
        const data = await productsApi.getProduct(parseInt(id, 10));
        if (isMounted) {
          setProduct(data);
          setError(null);
        }
      } catch {
        if (isMounted) {
          setError("Failed to load product details.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchProduct();
    return () => {
      isMounted = false;
    };
  }, [id]);

  if (loading) {
    return (
      <div className="mx-auto max-w-7xl px-4 py-8 animate-pulse">
        <div className="flex flex-col md:flex-row gap-8">
          <div className="w-full md:w-1/2 h-96 bg-muted rounded-xl" />
          <div className="w-full md:w-1/2 space-y-4 pt-4">
            <div className="h-4 w-24 bg-muted rounded-full" />
            <div className="h-8 w-3/4 bg-muted rounded" />
            <div className="h-6 w-32 bg-muted rounded" />
            <div className="h-24 w-full bg-muted rounded" />
            <div className="h-12 w-48 bg-muted rounded" />
          </div>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="mx-auto max-w-7xl px-4 py-24 text-center">
        <h2 className="text-2xl font-bold text-foreground mb-4">
          {error || "Product not found"}
        </h2>
        <Link
          to="/"
          className = "text-foreground/70 hover:text-foreground font-medium"
        >
          &larr; Back to catalog
        </Link>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-7xl px-4 py-8">
      <div className="mb-6">
        <Link
          to="/"
          className="text-sm font-medium opacity-70 hover:opacity-100 transition-opacity"
        >
          &larr; Back to catalog
        </Link>
      </div>

      <div className="flex flex-col md:flex-row gap-8 lg:gap-12">
        {/* Product Image */}
        <div className="w-full md:w-1/2">
          <div className="relative aspect-square w-full overflow-hidden rounded-2xl bg-muted">
            <img
              src={product.primaryImageUrl}
              alt={product.name}
              className="h-full w-full object-contain"
            />
          </div>
        </div>

        {/* Product Info */}
        <div className="w-full md:w-1/2 flex flex-col justify-center">
          {product.categoryName && (
            <span className="inline-flex items-center rounded-full bg-muted px-2.5 py-0.5 text-xs font-semibold text-foreground/50 mb-4 w-fit">
              {product.categoryName}
            </span>
          )}
          <h1 className="text-3xl font-bold tracking-tight text-foreground sm:text-4xl mb-4">
            {product.name}
          </h1>
          <div className="text-2xl font-semibold text-foreground mb-6">
            ${product.price}
          </div>

          <div className="prose prose-sm sm:prose-base dark:prose-invert text-foreground/80 mb-8">
            <p>{product.description}</p>
          </div>

          <div className="flex items-center gap-4 border-t border-border pt-8">
            <div className="flex items-center rounded-md border border-border">
              <button
                className="px-3 py-2 text-foreground/70 hover:text-foreground transition-colors"
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
              >
                -
              </button>
              <span className="w-12 text-center font-medium">{quantity}</span>
              <button
                className="px-3 py-2 text-foreground/70 hover:text-foreground transition-colors"
                onClick={() => setQuantity(quantity + 1)}
              >
                +
              </button>
            </div>

            <button
              disabled={isAdding}
              onClick={async () => {
                try {
                  setIsAdding(true);
                  await addItem(product.id, quantity);
                  toast.success(`Added ${quantity} ${product.name} to cart`);
                } catch {
                  toast.error("Failed to add to cart");
                } finally {
                  setIsAdding(false);
                }
              }}
              className="flex-1 rounded-none bg-foreground px-8 py-3 text-base font-semibold text-background transition-opacity hover:opacity-90 disabled:opacity-50 sm:flex-none"
            >
              {isAdding ? "Adding..." : "Add to Cart"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
