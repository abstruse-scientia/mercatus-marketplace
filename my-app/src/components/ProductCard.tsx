"use client";
import { Link } from "react-router-dom";
import type { Product } from "../types";

export default function ProductCard({ product }: { product: Product }) {
  return (
    <div className="card-modern relative mx-auto flex w-full min-w-0 max-w-[18rem] flex-col transition-transform duration-200 hover:-translate-y-1 sm:max-w-none">
      <Link to={`/products/${product.id}`} className="block h-full group">
        {/* Top: Image */}
        <div className="group relative h-36 w-full overflow-hidden bg-muted cursor-zoom-in sm:h-44 md:h-48">
          <img
            src={product.primaryImageUrl}
            alt={product.name}
            className="h-full w-full select-none object-contain transition-transform duration-300 ease-out will-change-transform group-hover:scale-110"
            loading="lazy"
          />
          {product.categoryName && (
            <span className="absolute top-2 right-2 rounded-full bg-background/80 px-2 py-1 text-xs font-semibold text-foreground backdrop-blur-sm">
              {product.categoryName}
            </span>
          )}
        </div>

        {/* Middle: Content */}
        <div className="flex flex-col gap-2 p-4 sm:p-5">
          <h3 className="text-base font-semibold text-card-foreground sm:text-lg">
            {product.name}
          </h3>
          <p className="text-sm text-card-foreground opacity-80 line-clamp-3">
            {product.description}
          </p>
        </div>
      </Link>

      {/* Bottom: Actions */}
      <div className="flex items-center justify-between border-t border-border px-4 py-3">
        <span className="text-base font-bold text-emerald-600 dark:text-emerald-400">
          ${product.price}
        </span>
        <button
          type="button"
          onClick={() => {
            // TODO: call addToCart API (or prompt login)
            console.log("Add to cart", product.id);
          }}
          className="inline-flex items-center justify-center rounded-md bg-accent px-3 py-2 text-sm font-medium text-background transition-colors z-10 hover:opacity-90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent/20"
        >
          Add to cart
        </button>
      </div>
    </div>
  );
}
