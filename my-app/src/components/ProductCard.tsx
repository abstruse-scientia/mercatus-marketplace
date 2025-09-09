"use client";
type Product = {
  productId: number;
  name: string;
  description: string;
  price: number;
  popularity: string;
  imageUrl: string;
};

export default function ProductCard({ product }: { product: Product }) {
  return (
    <div className="card-modern relative mx-auto flex w-full min-w-0 max-w-[18rem] flex-col transition-transform duration-200 hover:-translate-y-1 sm:max-w-none">
      {/* Top: Image */}
      <div className="group relative h-36 w-full overflow-hidden bg-muted cursor-zoom-in sm:h-44 md:h-48">
        <img
          src={product.imageUrl}
          alt={product.name}
          className="h-full w-full select-none object-contain transition-transform duration-300 ease-out will-change-transform group-hover:scale-110"
          loading="lazy"
        />
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

      {/* Bottom: Actions */}
      <div className="flex items-center justify-between border-t border-border px-4 py-3">
        <span className="text-base font-bold text-emerald-600 dark:text-emerald-400">
          ${product.price}
        </span>
        <button
          type="button"
          className="inline-flex items-center justify-center rounded-md bg-accent px-3 py-2 text-sm font-medium text-background transition-colors hover:opacity-90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent/20"
        >
          Add to cart
        </button>
      </div>
    </div>
  );
}
