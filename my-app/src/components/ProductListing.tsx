import SearchBar from "./SearchBar";
import FilterDropdown from "./FilterDropDown";
import ProductCard from "./ProductCard";
import PaginationControl from "./PaginationControl";
import CategoryFilterSidebar from "./CategoryFilterSidebar";
import type { PageResponse, Product } from "../types";

const sortOptions = [
  { label: "Newest", value: "createdAt,desc" },
  { label: "Price Low to High", value: "price,asc" },
  { label: "Price High to Low", value: "price,desc" },
];

export type ProductListingProps = {
  pageResponse: PageResponse<Product> | null;
  page: number;
  onPageChange: (page: number) => void;
  sort: string;
  onSortChange: (sort: string) => void;
  search: string;
  onSearchChange: (search: string) => void;
  category: string | null;
  onCategoryChange: (category: string | null) => void;
};

export default function ProductListing({
  pageResponse,
  page,
  onPageChange,
  sort,
  onSortChange,
  search,
  onSearchChange,
  category,
  onCategoryChange,
}: ProductListingProps) {
  const selectedSortLabel =
    sortOptions.find((opt) => opt.value === sort)?.label || "Newest";

  function handleSearchChange(inputSearch: string) {
    onSearchChange(inputSearch);
    if (page !== 0) onPageChange(0); // Reset to first page on search
  }

  function handleSortChange(sortLabel: string) {
    const option = sortOptions.find((opt) => opt.label === sortLabel);
    if (option) {
      onSortChange(option.value);
      if (page !== 0) onPageChange(0);
    }
  }

  function handleCategoryChange(newCategory: string | null) {
    onSearchChange(""); // reset search when category changes
    onCategoryChange(newCategory);
    if (page !== 0) onPageChange(0);
  }

  return (
    <div className="h-full w-full px-4 md:px-8 py-8 flex items-start gap-8">
      {/* Sidebar - Desktop Only */}
      <CategoryFilterSidebar
        selectedCategoryName={category}
        onCategorySelect={handleCategoryChange}
      />

      {/* Main Content */}
      <main className="flex-1 min-w-0">
        <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3 mb-6">
          <div className="flex-1 w-full">
            <SearchBar
              placeHolder="Search cameras, lenses, accessories..."
              value={search}
              handleSearch={(value) => handleSearchChange(value)}
            />
          </div>
          <FilterDropdown
            options={sortOptions.map((opt) => opt.label)}
            selectedValue={selectedSortLabel}
            handleSort={(value) => handleSortChange(value)}
          />
        </div>

        <div
          id="products"
          className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-3"
        >
          {pageResponse?.content.map((p) => (
            <ProductCard key={p.id} product={p} />
          ))}
        </div>
        <PaginationControl
          page={pageResponse ? pageResponse.pageable.pageNumber + 1 : 1}
          totalPages={pageResponse?.totalPages || 1}
          onPageChange={(p) => onPageChange(p - 1)}
        />
      </main>
    </div>
  );
}
