import { useEffect, useState } from "react";
import { useDebounce } from "use-debounce";
import Hero from "./Hero";
import ProductListing from "./ProductListing";
import { productsApi } from "../api/products.api";
import type { PageResponse, Product } from "../types";

export default function Home() {
  const [productPage, setProductPage] = useState<PageResponse<Product> | null>(
    null,
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("createdAt,desc");
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<string | null>(null);
  const [debouncedSearch] = useDebounce(search, 300);

  useEffect(() => {
    let isMounted = true;
    const fetchProducts = async () => {
      try {
        setLoading(true);
        let data;

        if (debouncedSearch) {
          data = await productsApi.searchProducts(debouncedSearch, {
            page,
            size: 12,
            sort,
          });
        } else if (category) {
          data = await productsApi.listProductsByCategoryName(category, {
            page,
            size: 12,
            sort,
          });
        } else {
          // Default show all products
          data = await productsApi.listProducts({
            page,
            size: 12,
            sort,
          });
        }

        if (isMounted) {
          setProductPage(data);
          setError(null);
        }
      } catch {
        if (isMounted) {
          setError("Failed to load products. Please try again later.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchProducts();
    return () => {
      isMounted = false;
    };
  }, [page, sort, debouncedSearch, category]);

  return (
    <>
      <Hero />
      {error && <div className="text-center text-red-500 py-12">{error}</div>}
      {!error && (
        <div className={loading ? "opacity-50 pointer-events-none" : ""}>
          <ProductListing
            pageResponse={productPage}
            page={page}
            onPageChange={setPage}
            sort={sort}
            onSortChange={setSort}
            search={search}
            onSearchChange={(newSearch) => {
              setSearch(newSearch);
              setPage(0);
            }}
            category={category}
            onCategoryChange={(newCategory) => {
              setCategory(newCategory);
              setPage(0);
            }}
          />
        </div>
      )}
    </>
  );
}
