import { useState, useEffect, useMemo } from "react";

export function usePagination<T>(items: T[]) {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(() => {
    if (typeof window == "undefined") return 8;
    const w = window.innerWidth;
    if (w < 1024) return 6;
    if (w < 640) return 4;
    return 8;
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

  const totalPages = Math.max(1, Math.ceil(items.length / pageSize));

  const pageItems = useMemo(() => {
    const start = (page - 1) * pageSize;
    return items.slice(start, start + pageSize);
  }, [page, pageSize, items]);

  const go = (n: number) => setPage(Math.min(totalPages, Math.max(1, n)));

  return {page, pageSize, totalPages, pageItems, go, setPage, setPageSize}
}
