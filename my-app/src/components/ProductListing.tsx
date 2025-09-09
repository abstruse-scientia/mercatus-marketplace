import React, { useMemo, useState } from "react";

export default function ProductListing({ products }) {
  const [searchText, setSearchText] = useState("");
  const [selectedSort, setSelectedSort] = useState("Popularity");

  const filteredAndSortedProducts = useMemo(() => {
    if (!Array.isArray(products)) {
      return [];
    }

    let filteredProducts = products.filter(
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

  
  return <div className="hello">hell0</div>;
}
