import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { ChevronDown, Filter } from "lucide-react";

type FilterDropdownProps = {
  options: string[];
  selectedValue: string;
  handleSort: (value: string) => void;
  placeholder?: string;
  showIcon?: boolean;
};

export default function FilterDropdown({
  options,
  selectedValue,
  handleSort,
  placeholder = "All Categories",
  showIcon = true,
}: FilterDropdownProps) {
  const displayLabel = selectedValue?.trim().length
    ? selectedValue
    : placeholder;

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          className="h-10 rounded-full flex items-center gap-2 border border-border bg-background text-foreground hover:bg-muted px-4"
        >
          {showIcon && <Filter className="w-4 h-4 text-foreground/70" />}
          <span className="text-sm">{displayLabel}</span>
          <ChevronDown className="w-4 h-4 text-foreground/70" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent
        className="w-56 rounded-xl border border-border bg-card p-1 text-card-foreground shadow-lg shadow-black/5"
        sideOffset={8}
      >
        {options.map((opt) => (
          <DropdownMenuItem
            key={opt}
            onClick={() => handleSort(opt)}
            className={`rounded-md px-3 py-2 text-sm text-card-foreground hover:bg-muted focus:bg-muted hover:shadow-sm focus:shadow-sm hover:shadow-black/5 focus:shadow-black/5 outline-none cursor-pointer ${
              opt === selectedValue ? "bg-muted font-medium" : ""
            }`}
          >
            {opt}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
