import { products } from "@/data/products";
import Hero from "./Hero";
import ProductListing from "./ProductListing";

export default function Home() {
  return (
    <>
      <Hero />
      <ProductListing products={products} />
    </>
  );
}
