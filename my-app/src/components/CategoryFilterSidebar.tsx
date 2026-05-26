import { useEffect, useState } from "react";
import { productsApi } from "../api/products.api";

type CategoryFilterSidebarProps = {
  selectedCategoryName: string | null;
  onCategorySelect: (categoryName: string | null) => void;
};

export default function CategoryFilterSidebar({
  selectedCategoryName,
  onCategorySelect,
}: CategoryFilterSidebarProps) {
  const [categories, setCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    const fetchCategories = async () => {
      try {
        setLoading(true);
        const categoryNames = await productsApi.getCategories();
        if (isMounted) {
          setCategories(categoryNames.sort((a, b) => a.localeCompare(b)));
        }
      } catch (err) {
        console.error("Failed to load categories for filtering.", err);
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchCategories();
    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <div className="hidden lg:block w-64 shrink-0">
      <div className="sticky top-24 flex flex-col gap-1 pr-6 border-r border-border min-h-[calc(100vh-8rem)]">
        <h3 className="font-semibold text-lg mb-4 text-foreground px-1">
          Categories
        </h3>

        {loading && categories.length === 0 ? (
          <div className="flex flex-col gap-2 opacity-50 px-1">
            {[1, 2, 3, 4, 5].map((n) => (
              <div
                key={n}
                className="h-8 w-full rounded-md bg-muted animate-pulse"
              />
            ))}
          </div>
        ) : (
          <div className="flex flex-col gap-1">
            <button
              onClick={() => onCategorySelect(null)}
              className={`text-left px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                selectedCategoryName === null
                  ? "bg-foreground text-background"
                  : "text-muted-foreground hover:bg-muted"
              }`}
            >
              All Products
            </button>
            {categories.map((category) => (
              <button
                key={category}
                onClick={() => onCategorySelect(category)}
                className={`text-left px-3 py-2 rounded-md text-sm font-medium transition-colors break-words ${
                  selectedCategoryName === category
                    ? "bg-foreground text-background"
                    : "text-muted-foreground hover:bg-muted text-pretty"
                }`}
              >
                {category}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
