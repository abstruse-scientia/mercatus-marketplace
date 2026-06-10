export default function Hero() {
  return (
    <section
      className="min-h-screen flex flex-col items-center justify-center bg-background border-b border-border"
      aria-labelledby="hero-heading"
    >
      <div className="px-6 text-center flex flex-col items-center w-full max-w-4xl">

        {/* Heading */}
        <h1
          id="hero-heading"
          className="text-[clamp(4rem,9vw,9rem)] font-light tracking-[-0.03em] leading-[0.95] text-foreground"
        >
          Mer<em className="italic">ca</em>tus
        </h1>

        {/* Tagline */}
        <p
          className="mx-auto mt-9 text-[clamp(14px,1.4vw,16px)] leading-[1.7] text-foreground/50"
          style={{ maxWidth: "44ch" }}
        >
          Antique and vintage cameras from the golden age of photography — each
          piece inspected and ready for your next roll.
        </p>

        {/* Buttons */}
        <div className="mt-12 flex items-center justify-center gap-3">
          <a
            href="#products"
            className="inline-flex items-center rounded-none bg-foreground text-background px-7 py-[13px] text-[13px] font-medium tracking-wide transition-opacity hover:opacity-85"
          >
            Shop the Collection
          </a>
          <a
            href="#learn"
            className="inline-flex items-center rounded-none border border-border bg-transparent text-foreground px-7 py-[13px] text-[13px] tracking-wide transition-colors hover:bg-muted"
          >
            Learn more
          </a>
        </div>

        {/* Stats row */}
        <div className="mt-12 pt-12 border-t border-border flex items-start justify-center gap-14 w-full">

          <div className="flex flex-col items-center gap-1">
            <span className="text-[28px] font-light tracking-tight text-foreground block mb-1">
              340+
            </span>
            <span className="text-[11px] uppercase tracking-[0.08em] text-foreground/40">
              Items in stock
            </span>
          </div>

          <div className="w-px self-stretch bg-border" />

          <div className="flex flex-col items-center gap-1">
            <span className="text-[28px] font-light tracking-tight text-foreground block mb-1">
              12 yrs
            </span>
            <span className="text-[11px] uppercase tracking-[0.08em] text-foreground/40">
              Curating since 2013
            </span>
          </div>

          <div className="w-px self-stretch bg-border" />

          <div className="flex flex-col items-center gap-1">
            <span className="text-[28px] font-light tracking-tight text-foreground block mb-1">
              4.9 ★
            </span>
            <span className="text-[11px] uppercase tracking-[0.08em] text-foreground/40">
              Avg. buyer rating
            </span>
          </div>

        </div>

      </div>
    </section>
  );
}