import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Mail,
  MapPin,
  Phone,
  Instagram,
  Twitter,
  MessageCircle,
} from "lucide-react";

export default function Contact() {
  return (
    <main className="mx-auto max-w-6xl px-4 py-10 md:py-16">
      <header className="mb-8 text-center">
        <h1
          id="hero-heading"
          className="font-extrabold tracking-tight text-5xl sm:text-6xl md:text-7xl"
        >
          <span className="bg-gradient-to-r from-foreground to-foreground/60 bg-clip-text text-transparent">
            Contact Us
          </span>
        </h1>
        <p className="mt-2 text-sm text-muted-foreground">
          Any question or remarks? Just write us a message.
        </p>
      </header>

      {/* Two-column on desktop, stacked on mobile */}
      {/* Join cards on both mobile (stacked) and desktop (side-by-side) */}
      <div className="grid gap-0 md:grid-cols-2">
        {/* Left: Contact information panel */}
        {/* Always-dark info panel regardless of theme */}
        <Card className="relative overflow-hidden bg-neutral-900 text-neutral-50 dark:bg-neutral-900 dark:text-neutral-50 border border-border border-b-0 rounded-b-none md:border-b md:rounded-b-xl md:rounded-r-none md:border-r-0">
          {/* subtle decorative blobs like the mock */}
          <div className="pointer-events-none absolute -right-10 -bottom-10 h-56 w-56 rounded-full bg-white/5" />
          <div className="pointer-events-none absolute -right-24 bottom-12 h-40 w-40 rounded-full bg-white/3" />

          <CardHeader>
            <CardTitle className="text-lg md:text-xl font-medium tracking-tight">
              Contact Information
            </CardTitle>
            <CardDescription className="text-neutral-300 text-sm md:text-[15px]">
              Share your thoughts with us!
            </CardDescription>
          </CardHeader>
          <CardContent className="grid gap-4">
            <div className="space-y-4 text-sm">
              {/* Phone */}
              <div className="flex items-start gap-4">
                <span className="mt-0.5 inline-flex h-8 w-8 items-center justify-center rounded-lg bg-white/10 ring-1 ring-white/15">
                  <Phone className="h-4 w-4 text-neutral-50" aria-hidden />
                </span>
                <div>
                  <p className="font-medium">Phone</p>
                  <p className="opacity-90">+1 (012) 3456 789</p>
                </div>
              </div>

              {/* Email */}
              <div className="flex items-start gap-4">
                <span className="mt-0.5 inline-flex h-8 w-8 items-center justify-center rounded-lg bg-white/10 ring-1 ring-white/15">
                  <Mail className="h-4 w-4 text-neutral-50" aria-hidden />
                </span>
                <div>
                  <p className="font-medium">Email</p>
                  <p className="opacity-90">demo@gmail.com</p>
                </div>
              </div>

              {/* Address */}
              <div className="flex items-start gap-4">
                <span className="mt-0.5 inline-flex h-8 w-8 items-center justify-center rounded-lg bg-white/10 ring-1 ring-white/15">
                  <MapPin className="h-4 w-4 text-neutral-50" aria-hidden />
                </span>
                <div>
                  <p className="font-medium">Address</p>
                  <p className="opacity-90">
                    132 Dartmouth Street Boston, MA 02116, United States
                  </p>
                </div>
              </div>
            </div>

            {/* Socials */}
            <div className="mt-2 flex items-center gap-4">
              <a
                href="#"
                aria-label="Twitter"
                className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-white/10 ring-1 ring-white/15 transition-colors hover:bg-white/15"
              >
                <Twitter className="h-4 w-4 text-neutral-50" />
              </a>
              <a
                href="#"
                aria-label="Instagram"
                className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-white/10 ring-1 ring-white/15 transition-colors hover:bg-white/15"
              >
                <Instagram className="h-4 w-4 text-neutral-50" />
              </a>
              <a
                href="#"
                aria-label="Message"
                className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-white/10 ring-1 ring-white/15 transition-colors hover:bg-white/15"
              >
                <MessageCircle className="h-4 w-4 text-neutral-50" />
              </a>
            </div>
          </CardContent>
          <CardFooter className="text-neutral-200/70">
            We'll use your info only to respond to your inquiry.
          </CardFooter>
        </Card>

        {/* Right: Contact form */}
        <Card className="bg-card text-card-foreground border border-border border-t-0 rounded-t-none md:border-t md:rounded-t-xl md:rounded-l-none md:border-l-0">
          <CardHeader className="border-b border-border">
            <CardTitle className="text-xl">Send a Message</CardTitle>
            <CardDescription>
              Fill out the form and we'll get back to you soon.
            </CardDescription>
          </CardHeader>
          <CardContent className="pt-6">
            <form className="grid gap-5">
              {/* Name */}
              <div className="grid gap-2">
                <label htmlFor="name" className="text-sm font-medium">
                  Name
                </label>
                <Input
                  id="name"
                  name="name"
                  type="text"
                  placeholder="John Doe"
                  className="border border-border focus-visible:border-accent focus-visible:ring-accent/40"
                />
              </div>

              {/* Email */}
              <div className="grid gap-2">
                <label htmlFor="email" className="text-sm font-medium">
                  Email
                </label>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="you@example.com"
                  className="border border-border focus-visible:border-accent focus-visible:ring-accent/40"
                />
              </div>

              {/* Mobile Number */}
              <div className="grid gap-2">
                <label htmlFor="mobileNumber" className="text-sm font-medium">
                  Mobile Number
                </label>
                <Input
                  id="mobileNumber"
                  name="mobileNumber"
                  type="tel"
                  placeholder="+1 012 345 6789"
                  className="border border-border focus-visible:border-accent focus-visible:ring-accent/40"
                />
              </div>

              {/* Message */}
              <div className="grid gap-2">
                <label htmlFor="message" className="text-sm font-medium">
                  Message
                </label>
                <textarea
                  id="message"
                  name="message"
                  rows={5}
                  placeholder="Write your message..."
                  className="placeholder:text-muted-foreground selection:bg-accent/20 selection:text-foreground dark:bg-transparent border border-border min-h-28 w-full min-w-0 rounded-md bg-transparent px-3 py-2 text-base shadow-xs transition-[color,box-shadow] outline-none disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm focus-visible:border-accent focus-visible:ring-accent/40 focus-visible:ring-[3px]"
                />
              </div>

              <div className="flex items-center justify-end">
                <Button
                  type="button"
                  variant="outline"
                  className="bg-accent text-background border-transparent hover:opacity-90"
                >
                  Send Message
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </main>
  );
}
