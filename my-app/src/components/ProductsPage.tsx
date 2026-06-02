import { useEffect, useState } from "react";
import { useSearchParams, Link } from "react-router-dom";
import { productsApi } from "../api/products.api";
import type { Product, PageResponse } from "../types";
import ProductCard from "./ProductCard";

export default function ProductsPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  // URL States
  const category = searchParams.get("category") || "all";
  const search = searchParams.get("search") || "";
  const page = parseInt(searchParams.get("page") || "0", 10);
  const sort = searchParams.get("sort") || "createdAt,desc";

  const [categories, setCategories] = useState<string[]>([]);
  const [productsPage, setProductsPage] =
    useState<PageResponse<Product> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isInitialMount, setIsInitialMount] = useState(true);


  // Fetch Categories
  useEffect(() => {
    let mounted = true;
    productsApi
      .getCategories()
      .then((data) => {
        if (mounted) setCategories(data);
      })
      .catch((err) => console.error("Failed to load categories", err));
    return () => {
      mounted = false;
    };
  }, []);

  // Fetch Products
  useEffect(() => {
    let mounted = true;
    setIsLoading(true);

    const fetchProducts = async () => {
      try {
        let res: PageResponse<Product>;
        const size = 12;

        if (search) {
          res = await productsApi.searchProducts(search, { page, size, sort });
        } else if (category && category !== "all") {
          res = await productsApi.listProductsByCategoryName(category, {
            page,
            size,
            sort,
          });
        } else {
          res = await productsApi.listProducts({ page, size, sort });
        }

        if (mounted) {
          setProductsPage(res);
          setIsInitialMount(false);
        }
      } catch (err) {
        console.error("Failed to fetch products", err);
        if (mounted) {
          console.log("CATCH - setting isInitialMount false");
          setIsInitialMount(false);
        }
      } finally {
        if (mounted) {
          setIsLoading(false);
        }
      }
    };

    fetchProducts();
    return () => {
      mounted = false;
    };
  }, [category, search, page, sort]);

  const updateParams = (updates: Record<string, string | null>) => {
    const nextParams = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (value === null) {
        nextParams.delete(key);
      } else {
        nextParams.set(key, value);
      }
    });
    setSearchParams(nextParams);
  };


  if (isInitialMount) {
    return (
      <div className="flex min-h-[calc(100vh-4rem)] items-center justify-center bg-background">
        <div className="font-mono text-sm tracking-widest text-foreground">
          {"> ESTABLISHING ARCHIVE CONNECTION..."}
        </div>
      </div>
    );
  }

  const allCategories = ["all", ...categories];

  return (
    <div className="min-h-screen bg-background px-4 py-8 md:px-8">
      {/* Top-Level Control Board */}
      <div className="mx-auto max-w-7xl mb-12">
        <div className="mb-8">
          <nav className="font-mono text-[10px] uppercase tracking-widest opacity-60 mb-4">
            <Link to="/home" className="hover:opacity-100 transition-opacity">
              HOME
            </Link>{" "}
            / COLLECTION
          </nav>
          <h1 className="text-4xl font-bold tracking-tight uppercase">
            THE ARCHIVE
          </h1>
        </div>

        <div className="flex flex-wrap gap-2 mb-6">
          {allCategories.map((cat) => {
            const isActive = category === cat;
            return (
              <button
                key={cat}
                onClick={() => updateParams({ category: cat, page: "0" })}
                className={`rounded-none text-xs font-semibold tracking-wider uppercase px-4 py-2 border border-border transition-colors ${
                  isActive
                    ? "bg-foreground text-background"
                    : "bg-transparent text-foreground hover:bg-muted"
                }`}
              >
                {cat}
              </button>
            );
          })}
        </div>

        <div className="flex items-center gap-4 pb-4 border-b border-border/40">
          <select
            value={sort}
            onChange={(e) => updateParams({ sort: e.target.value, page: "0" })}
            className="bg-transparent border-none text-xs font-mono uppercase focus:ring-0 cursor-pointer appearance-none outline-none text-foreground/80 hover:text-foreground transition-colors"
          >
            <option value="createdAt,desc">NEWEST TO OLDEST</option>
            <option value="createdAt,asc">OLDEST TO NEWEST</option>
            <option value="price,desc">PRICE: HIGH TO LOW</option>
            <option value="price,asc">PRICE: LOW TO HIGH</option>
          </select>
        </div>
      </div>

      {/* Retain and Fade grid container */}
      <div
        className={`mx-auto max-w-7xl transition-opacity duration-300 ${isLoading ? "opacity-40 pointer-events-none" : "opacity-100"}`}
      >
        {productsPage?.content.length === 0 ? (
          <div className="py-24 text-center font-mono text-sm tracking-widest uppercase opacity-60">
            [ NO DATA FOUND ]
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
            {productsPage?.content.map((product) => (
              <ProductCard key = {product.id} product={product}/>
            ))}
          </div>
        )}

        {/* Minimalist Pagination */}
        {productsPage && productsPage.totalPages > 1 && (
          <div className="mt-16 flex items-center justify-center gap-6 font-mono text-sm tracking-wider">
            <button
              disabled={page === 0}
              onClick={() => updateParams({ page: (page - 1).toString() })}
              className="hover:opacity-100 opacity-60 disabled:opacity-30 disabled:cursor-not-allowed transition-opacity uppercase"
            >
              {"< PREV"}
            </button>

            <div className="flex gap-4">
              {Array.from({ length: productsPage.totalPages }).map((_, i) => (
                <button
                  key={i}
                  onClick={() => updateParams({ page: i.toString() })}
                  className={`transition-all ${i === page ? "border-b-2 border-foreground opacity-100" : "opacity-60 hover:opacity-100"}`}
                >
                  {i + 1}
                </button>
              ))}
            </div>

            <button
              disabled={page === productsPage.totalPages - 1}
              onClick={() => updateParams({ page: (page + 1).toString() })}
              className="hover:opacity-100 opacity-60 disabled:opacity-30 disabled:cursor-not-allowed transition-opacity uppercase"
            >
              {"NEXT >"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
