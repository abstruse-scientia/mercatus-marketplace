import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { productsApi } from "../api/products.api";
import type { CategorySummary, Product } from "../types";
import ProductCard from "./ProductCard";
import toast from "react-hot-toast";

export default function Home() {
  const [categories, setCategories] = useState<CategorySummary[]>([]);
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [allProducts, setAllProducts] = useState<Product[]>([]);

  useEffect(() => {
    let isMounted = true;
    const fetchHomeData = async () => {
      try {
        const [prods, catSummaries] = await Promise.all([
          productsApi.listProducts({
            page: 0,
            size: 20,
            sort: "createdAt,desc",
          }),
          productsApi.getCategorySummaries(),
        ]);

        if (isMounted) {
          setAllProducts(prods.content);
          setFeaturedProducts(prods.content.slice(0, 3));
          setCategories(catSummaries.slice(-3));
        }
      } catch (error) {
        console.error("Failed to load home data", error);
      }
    };
    fetchHomeData();
    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <main>
      {/* SECTION 1 — HERO */}
      <section className="flex flex-col items-center justify-center min-h-[88vh] border-b border-border px-6">
        <h1 className="text-[clamp(3.6rem,8vw,7.5rem)] font-light tracking-[-0.035em] leading-[0.95] text-center m-0">
          Mer<em className="italic">ca</em>tus
        </h1>

        <p className="text-[clamp(14px,1.4vw,16px)] text-foreground opacity-50 max-w-[44ch] text-center leading-[1.7] mb-[48px] mt-[24px]">
          Curated antique and vintage cameras from the golden age of
          photography. Discover 20th‑century film cameras, classic lenses, and
          accessories.
        </p>

        <div className="flex flex-col sm:flex-row gap-3">
          <Link
            to="/products"
            className="bg-foreground text-background border-none rounded-[2px] px-[28px] py-[13px] text-[13px] font-medium transition-opacity hover:opacity-90"
          >
            Shop the collection
          </Link>
          <a
            href="#learn"
            className="bg-transparent text-foreground border border-border rounded-[2px] px-[28px] py-[13px] text-[13px] transition-colors hover:bg-[var(--muted)]"
          >
            Learn more
          </a>
        </div>
      </section>

      {/* SECTION 2 — CATEGORIES */}
      <section className="px-4 md:px-12 pt-12 md:pt-20 pb-12 md:pb-24">
        <div className="flex justify-between items-baseline">
          <div>
            <div className="text-[11px] font-medium tracking-[0.14em] uppercase text-foreground opacity-40 mb-[8px]">
              Explore
            </div>
            <h2 className="text-[clamp(1.6rem,2.5vw,2.2rem)] font-normal tracking-[-0.02em] m-0">
              Shop by category
            </h2>
          </div>
          <Link
            to="/products"
            className="text-[12px] font-medium tracking-[0.08em] uppercase text-foreground opacity-40 border-b border-border pb-[2px] transition-opacity hover:opacity-100"
          >
            View all categories →
          </Link>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mt-8 md:mt-16">
          {categories.map((cat, idx) => {
            const categoryImage = allProducts.find(
              (p) =>
                p.categoryName?.toLowerCase() ===
                cat.categoryName?.toLowerCase(),
            )?.primaryImageUrl;
            return (
              <Link
                to={`/products?category=${encodeURIComponent(cat.categoryName)}`}
                key={idx}
                className="group block"
              >
                <div className="aspect-[3/4] w-full overflow-hidden bg-[var(--muted)] m-0 p-0 border-0 shadow-none rounded-none flex items-center justify-center relative">
                  {categoryImage ? (
                    <img
                      src={categoryImage}
                      alt={cat.categoryName}
                      className="w-full h-full object-cover origin-center transition-transform duration-[600ms] ease-[cubic-bezier(0.25,0.46,0.45,0.94)] group-hover:scale-[1.04]"
                    />
                  ) : (
                    <div className="text-foreground opacity-20">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="48"
                        height="48"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="1.5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      >
                        <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z" />
                        <circle cx="12" cy="13" r="3" />
                      </svg>
                    </div>
                  )}
                </div>
                <div className="flex justify-between items-baseline pt-[20px]">
                  <span className="text-[12px] font-medium tracking-[0.12em] uppercase">
                    {cat.categoryName}
                  </span>
                  <span className="text-[11px] text-foreground opacity-35">
                    {cat.productCount}
                  </span>
                </div>
              </Link>
            );
          })}
          {categories.length === 0 && (
            <div className="col-span-3 text-foreground opacity-50 text-sm">
              No categories found.
            </div>
          )}
        </div>
      </section>

      {/* SECTION 3 — FEATURED PICKS */}
      <section className="px-4 md:px-12 pt-12 md:pt-20 pb-12 md:pb-24 border-b border-border">
        <div className="flex justify-between items-baseline">
          <div>
            <div className="text-[11px] font-medium tracking-[0.14em] uppercase text-foreground opacity-40 mb-[8px]">
              Editor's selection
            </div>
            <h2 className="text-[clamp(1.6rem,2.5vw,2.2rem)] font-normal tracking-[-0.02em] m-0">
              Recently arrived
            </h2>
          </div>
          <Link
            to="/products"
            className="text-[12px] font-medium tracking-[0.08em] uppercase text-foreground opacity-40 border-b border-border pb-[2px] transition-opacity hover:opacity-100"
          >
            View all products →
          </Link>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mt-8 md:mt-12">
          {featuredProducts.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
          {featuredProducts.length === 0 && (
            <div className="col-span-3 text-foreground opacity-50 text-sm">
              No products found.
            </div>
          )}
        </div>
      </section>

      {/* SECTION 4 — NEWSLETTER */}
      <section className="px-4 md:px-12 py-12 md:py-20 border-b border-border flex flex-col md:flex-row justify-between items-start md:items-center gap-8 md:gap-16">
        <div className="max-w-[400px]">
          <div className="text-[11px] font-medium tracking-[0.14em] uppercase text-foreground opacity-40 mb-[12px]">
            Stay in the loop
          </div>
          <h2 className="text-[clamp(1.8rem,2.5vw,2.4rem)] font-normal tracking-[-0.02em] leading-[1.15] m-0">
            New arrivals <br /> every week
          </h2>
        </div>

        <div className="flex flex-col w-full md:w-auto flex-1 max-w-[440px]">
          <form
            className="flex border border-border rounded-none bg-card"
            onSubmit={(e) => {
              e.preventDefault();
              toast.success("You have subscribed successfully!");
              (e.target as HTMLFormElement).reset();
            }}
          >
            <input
              type="email"
              placeholder="Your email address"
              required
              className="flex-1 px-[18px] py-[14px] bg-transparent border-none outline-none text-[13px] text-foreground placeholder:text-foreground placeholder:opacity-30"
            />
            <button
              type="submit"
              className="bg-foreground text-background border-none px-[24px] py-[14px] text-[12px] font-semibold tracking-[0.06em] uppercase rounded-none cursor-pointer transition-opacity hover:opacity-90 shrink-0"
            >
              Subscribe
            </button>
          </form>
          <div className="text-[11px] text-foreground opacity-30 mt-[10px]">
            No spam. Unsubscribe any time.
          </div>
        </div>
      </section>
    </main>
  );
}
