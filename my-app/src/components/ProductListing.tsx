import React, { useMemo, useState } from "react";
import { usePagination } from "./usePagination";
import SearchBar from "./SearchBar";
import FilterDropdown from "./FilterDropDown";
import ProductCard from "./ProductCard";
import PaginationControl from "./PaginationControl";

const sortList = ["Popularity", "Price Low to High", "Price High to Low"];

export default function ProductListing({ products }) {
  const [searchText, setSearchText] = useState("");
  const [selectedSort, setSelectedSort] = useState("Popularity");

  const filteredAndSortedProducts = useMemo(() => {
    if (!Array.isArray(products)) {
      return [];
    }

    const filteredProducts = products.filter(
      (product) =>
        product.name.toLowerCase().includes(searchText.toLowerCase()) ||
        product.description.toLowerCase().includes(searchText.toLowerCase())
    );

    return filteredProducts.slice().sort((a, b) => {
      switch (selectedSort) {
        case "Price Low to High":
          return parseFloat(a.price) - parseFloat(b.price);
        case "Price Hight to Low":
          return parseFloat(b.price) - parseFloat(a.price);
        case "Populartiy":
          return parseFloat(b.popularity) - parseFloat(a.popularity);
        default:
          return parseInt(b.popularity) - parseFloat(a.popularity);
      }
    });
  }, [products, searchText, selectedSort]);

  const { page, totalPages, pageItems, go } = usePagination(
    filteredAndSortedProducts
  );

  function handleSearchChange(inputSearch: string) {
    setSearchText(inputSearch);
  }

  function handleSortChange(sortType: string) {
    setSelectedSort(sortType);
  }
  return (
    <>
      <div className="mx-auto max-w-5xl px-4 mt-2">
        <div className="flex items-center gap-3 mb-6">
          <div className="flex-1">
            <SearchBar
              placeHolder="Search cameras, lenses, accessories..."
              value={searchText}
              handleSearch={(value) => handleSearchChange(value)}
            />
          </div>
          <FilterDropdown
            options={sortList}
            selectedValue={selectedSort}
            handleSort={(value) => handleSortChange(value)}
          />
        </div>
      </div>
      <main className="mx-auto max-w-7xl px-4 py-8">
        <div
          id="products"
          className="grid grid-cols-1 gap-6 sm:grid-cols-3 lg:grid-cols-3 xl:grid-cols-4"
        >
          {pageItems.map((p) => (
            <ProductCard key={p.productId} product={p} />
          ))}
        </div>
        <PaginationControl
          page={page}
          totalPages={totalPages}
          onPageChange={go}
        />
      </main>
    </>
  );
}
