import { useEffect, useMemo, useState } from "react";
import Header from "./components/Header";
import Hero from "./components/Hero";
import ProductCard from "./components/ProductCard";
import { products } from "./data/products";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import SearchBar from "./components/SearchBar";
import DropDown from "./components/DropDown";

function App() {
  // sorting
  const sortOptions = ["Low to High", "High to Low", "Popularity"] as const;
  type SortOption = (typeof sortOptions)[number];
  const [sort, setSort] = useState<SortOption>("Low to High");

  // search
  const [query, setQuery] = useState("");

  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(() => {
    if (typeof window === "undefined") return 8;
    const w = window.innerWidth;
    if (w < 640) return 4; // mobile: up to 2x2
    if (w < 1024) return 6; // tablet: 3x2
    return 8; // desktop: 4x2
  });

  useEffect(() => {
    const calc = () => {
      const w = window.innerWidth;
      const next = w < 640 ? 4 : w < 1024 ? 6 : 8;
      setPageSize((prev) => (prev === next ? prev : next));
    };
    window.addEventListener("resize", calc);
    return () => window.removeEventListener("resize", calc);
  }, []);

  const filteredProducts = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return products;
    return products.filter((p) =>
      [p.name, p.description].some((t) => t.toLowerCase().includes(q))
    );
  }, [query]);

  const sortedProducts = useMemo(() => {
    const list = [...filteredProducts];
    switch (sort) {
      case "Low to High":
        return list.sort((a, b) => a.price - b.price);
      case "High to Low":
        return list.sort((a, b) => b.price - a.price);
      case "Popularity":
        return list.sort(
          (a, b) => parseInt(b.popularity) - parseInt(a.popularity)
        );
      default:
        return list;
    }
  }, [sort, filteredProducts]);

  const total = sortedProducts.length;
  const totalPages = Math.max(1, Math.ceil(total / pageSize));

  const pageItems = useMemo(() => {
    const start = (page - 1) * pageSize;
    return sortedProducts.slice(start, start + pageSize);
  }, [page, pageSize, sortedProducts]);

  // Ensure page is within bounds when pageSize changes
  useEffect(() => {
    if (page > totalPages) setPage(totalPages);
  }, [page, pageSize, totalPages]);

  const go = (n: number) => setPage(Math.min(totalPages, Math.max(1, n)));

  return (
    <>
      <Header />
      <Hero />
      <div className="mx-auto max-w-5xl px-4 -mt-2">
        <div className="flex items-center gap-3">
          <div className="flex-1">
            <SearchBar
              placeHolder="Search cameras, lenses, accessories..."
              value={query}
              handleSearch={setQuery}
            />
          </div>
          <DropDown
            options={[...sortOptions]}
            selectedValue={sort}
            handleSort={(val) => {
              setSort(val as SortOption);
              setPage(1);
            }}
            placeholder="All Categories"
            showIcon
          />
        </div>
      </div>
      <main className="mx-auto max-w-7xl px-4 py-8">
        <h1 className="sr-only">Products</h1>

        <div
          id="products"
          className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
        >
          {pageItems.map((p) => (
            <ProductCard key={p.productId} product={p} />
          ))}
        </div>

        <Pagination className="mt-8">
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  if (page > 1) go(page - 1);
                }}
                aria-disabled={page === 1}
                className={
                  page === 1 ? "pointer-events-none opacity-50" : undefined
                }
              />
            </PaginationItem>

            {Array.from({ length: totalPages }).map((_, i) => {
              const n = i + 1;
              return (
                <PaginationItem key={n}>
                  <PaginationLink
                    href="#"
                    isActive={page === n}
                    onClick={(e) => {
                      e.preventDefault();
                      go(n);
                    }}
                  >
                    {n}
                  </PaginationLink>
                </PaginationItem>
              );
            })}

            <PaginationItem>
              <PaginationNext
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  if (page < totalPages) go(page + 1);
                }}
                aria-disabled={page === totalPages}
                className={
                  page === totalPages
                    ? "pointer-events-none opacity-50"
                    : undefined
                }
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      </main>
    </>
  );
}

export default App;
