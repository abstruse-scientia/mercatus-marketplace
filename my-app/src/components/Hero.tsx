export default function Hero() {
  return (
    <section
      className="relative overflow-hidden"
      aria-labelledby="hero-heading"
    >
      {/* Decorative subtle radial highlight (not a boxed gradient) */}
      <div
        aria-hidden
        className="pointer-events-none absolute inset-0 opacity-60 [background:radial-gradient(circle_at_center,theme(colors.muted/40),transparent_60%)] dark:opacity-40"
      />
      <div className="relative mx-auto max-w-5xl px-4 py-16 sm:py-20 md:py-24 text-center">
        <h1
          id="hero-heading"
          className="font-extrabold tracking-tight text-5xl sm:text-6xl md:text-7xl"
        >
          <span className="bg-gradient-to-r from-foreground to-foreground/60 bg-clip-text text-transparent">
            Mercatus
          </span>
        </h1>
        <p className="mx-auto mt-5 max-w-2xl text-balance text-base leading-relaxed text-muted-foreground sm:text-lg">
          Curated antique and vintage cameras from the golden age of
          photography. Discover 20th‑century film cameras, classic lenses, and
          accessories— carefully inspected and ready for your next roll.
        </p>
        <div className="mt-8 flex items-center justify-center gap-3">
          <a
            href="#products"
            className="inline-flex items-center rounded-md bg-foreground px-4 py-2 text-sm font-medium text-background shadow hover:opacity-90 transition"
          >
            Shop the collection
          </a>
          <a
            href="#learn"
            className="inline-flex items-center rounded-md border border-input bg-transparent px-4 py-2 text-sm font-medium hover:bg-muted/60 transition"
          >
            Learn more
          </a>
        </div>
      </div>
    </section>
  );
}
