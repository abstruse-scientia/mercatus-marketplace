import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationPrevious,
  PaginationLink,
  PaginationNext,
} from "./ui/pagination";

type PaginationControlProps = {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
};

export default function PaginationControl({
  page,
  totalPages,
  onPageChange,
}: PaginationControlProps) {
  return (
    <Pagination className="mt-8">
      <PaginationContent>
        {/* Previous button */}
        <PaginationItem>
          <PaginationPrevious
            href="#"
            onClick={(e) => {
              e.preventDefault();
              if (page > 1) onPageChange(page - 1);
            }}
            aria-disabled={page === 1}
            className={
              page === 1 ? "pointer-events-none opactiy-50" : undefined
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
                  onPageChange(n);
                }}
              >
                {n}
              </PaginationLink>
            </PaginationItem>
          );
        })}
        {/* Next Button */}
        <PaginationItem>
          <PaginationNext
            href="#"
            onClick={(e) => {
              e.preventDefault();
              if (page < totalPages) onPageChange(page + 1);
            }}
            aria-disabled={page === totalPages}
            className={
              page === totalPages ? "pointer-events-none opacity-50" : undefined
            }
          />
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  );
}
