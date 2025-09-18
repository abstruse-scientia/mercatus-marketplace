import { Camera, Shield, Users, Leaf, Globe2 } from "lucide-react";

export default function About() {
  return (
    <main className="bg-background text-foreground">
      {/* Hero */}
      <section className="relative overflow-hidden">
        <div
          aria-hidden
          className="pointer-events-none absolute inset-0 opacity-60 [background:radial-gradient(circle_at_center,theme(colors.muted/40),transparent_60%)] dark:opacity-40"
        />
        <div className="relative mx-auto max-w-5xl px-4 py-14 sm:py-18 md:py-22 text-center">
          <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold tracking-tight">
            <span className="bg-gradient-to-r from-foreground to-foreground/60 bg-clip-text text-transparent px-2">
              Who we are?
            </span>
          </h1>
          <p className="mx-auto mt-4 max-w-2xl text-balance text-base leading-relaxed text-foreground/70">
            We curate iconic film cameras, lenses, and accessories—meticulously
            inspected and ready to shoot. Our mission is to preserve the craft
            and joy of analog photography.
          </p>
        </div>
      </section>

      {/* Story card */}
      <section className="mx-auto max-w-5xl px-4">
        <article className="card-modern p-6 sm:p-8">
          <div className="space-y-2">
            <div className="flex items-center gap-3">
              <Camera className="h-5 w-5 shrink-0" />
              <h2 className="text-xl sm:text-2xl font-semibold tracking-tight leading-none">
                Our Story
              </h2>
            </div>
            <p className="text-foreground/75 leading-relaxed">
              Born from a love of craftsmanship and the timeless look of film,
              Mercatus started as a weekend project and grew into a marketplace.
              We source, restore, and test every item so you can focus on making
              images that matter.
            </p>
          </div>
        </article>
      </section>

      {/* Values grid */}
      <section className="mx-auto max-w-5xl px-4 mt-8 grid gap-4 sm:grid-cols-2">
        <div className="card-modern p-6">
          <div className="flex items-center gap-3">
            <Shield className="h-5 w-5" />
            <h3 className="font-medium">Quality & Assurance</h3>
          </div>
          <p className="mt-2 text-foreground/70">
            Each product is graded, cleaned, and tested. Transparent condition
            notes and accurate photos come standard.
          </p>
        </div>
        <div className="card-modern p-6">
          <div className="flex items-center gap-3">
            <Users className="h-5 w-5" />
            <h3 className="font-medium">Community First</h3>
          </div>
          <p className="mt-2 text-foreground/70">
            We support local repair techs and educators, and we reinvest in
            programs that keep film alive.
          </p>
        </div>
        <div className="card-modern p-6">
          <div className="flex items-center gap-3">
            <Leaf className="h-5 w-5" />
            <h3 className="font-medium">Sustainable by Design</h3>
          </div>
          <p className="mt-2 text-foreground/70">
            Extending the life of well‑made gear reduces waste and honors the
            materials and makers behind them.
          </p>
        </div>
        <div className="card-modern p-6">
          <div className="flex items-center gap-3">
            <Globe2 className="h-5 w-5" />
            <h3 className="font-medium">Global Access</h3>
          </div>
          <p className="mt-2 text-foreground/70">
            We ship worldwide and share knowledge openly so anyone can step into
            film with confidence.
          </p>
        </div>
      </section>

      {/* CTA */}
      <section className="mx-auto max-w-5xl px-4 mt-8 mb-0 pb-16">
        <div className="card-modern p-6 sm:p-8 flex flex-col sm:flex-row items-center justify-between gap-4 ">
          <div>
            <h4 className="text-lg font-semibold">Have questions?</h4>
            <p className="text-foreground/70">
              Reach out and we’ll help you pick the right kit.
            </p>
          </div>
          <a
            href="/contact"
            className="inline-flex items-center rounded-md bg-foreground px-4 py-2 text-sm font-medium text-background shadow hover:opacity-90 transition"
          >
            Contact us
          </a>
        </div>
      </section>
    </main>
  );
}
