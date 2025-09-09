import React from "react";
import { Input } from "./ui/input";

type SearchBarProps = {
  placeHolder: string;
  value: string;
  handleSearch: (value: string) => void;
};

export default function SearchBar({
  placeHolder,
  value,
  handleSearch,
}: SearchBarProps) {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    handleSearch(event.target.value);
  };

  return (
    <div className="relative mx-auto w-full max-w-2xl">
      <Input
        type="search"
        aria-label={placeHolder || "Search"}
        placeholder={placeHolder}
        value={value}
        onChange={handleChange}
        className="w-full h-10 rounded-full px-4 shadow-sm border focus-visible:ring-0 focus-visible:border-black"
      />
    </div>
  );
}
