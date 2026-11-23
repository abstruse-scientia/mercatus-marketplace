import { useRouteError, isRouteErrorResponse, Link } from "react-router-dom";
import errorImage from "../assets/images/error-page-pic.png";
import { Button } from "./ui/button";
import { Home, RefreshCw, ArrowLeft } from "lucide-react";

export default function ErrorPage() {
  const error = useRouteError();

  let statusCode = 404;
  let statusTitle = "Page Not Found";
  let statusMessage =
    "The page you're looking for has vanished like an old photograph. Perhaps it was never developed.";

  if (isRouteErrorResponse(error)) {
    statusCode = error.status;
    statusTitle =
      error.statusText ||
      (error.status === 404 ? "Page Not Found" : "Application Error");

    if (error.status === 404) {
      statusMessage =
        "The page or archive item you're looking for cannot be found or may have been relocated.";
    } else if (error.data && typeof error.data === "string") {
      statusMessage = error.data;
    } else if (error.data?.message) {
      statusMessage = error.data.message;
    } else if (error.status === 500) {
      statusMessage =
        "An internal server error occurred while processing your request. Please try again later.";
    }
  } else if (error instanceof Error) {
    statusCode = 500;
    statusTitle = "Unexpected Application Error";
    statusMessage =
      error.message ||
      "An unexpected error interrupted your session. Please try reloading the page.";
  } else if (error && typeof error === "object" && "message" in error) {
    statusCode = 500;
    statusTitle = "Unexpected Application Error";
    statusMessage = String((error as any).message);
  }

  const is404 = statusCode === 404;

  return (
    <div
      className="min-h-screen w-full bg-cover bg-center bg-no-repeat relative flex items-center p-6 sm:p-12 md:p-16 overflow-hidden"
      style={{
        backgroundImage: `linear-gradient(to right, rgba(0,0,0,0.92) 0%, rgba(0,0,0,0.78) 50%, rgba(0,0,0,0.3) 100%), url(${errorImage})`,
      }}
    >
      <div className="max-w-xl relative z-10 w-full space-y-6">
        {/* Error Code */}
        <div className="space-y-1">
          <span className="font-mono text-xs uppercase tracking-widest text-zinc-400">
            System Status
          </span>
          <h1 className="text-6xl sm:text-7xl md:text-8xl font-black text-destructive font-mono leading-none tracking-tight">
            {statusCode}
          </h1>
        </div>

        {/* Title & Dynamic Error Message Slot */}
        <div className="space-y-2">
          <h2 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
            {statusTitle}
          </h2>
          <p className="text-zinc-300 font-mono text-sm leading-relaxed max-w-lg break-words">
            {statusMessage}
          </p>
        </div>

        {/* Action Controls */}
        <div className="flex flex-wrap items-center gap-3 pt-2">
          <Button
            asChild
            size="lg"
            className="bg-white text-zinc-900 hover:bg-zinc-100 rounded-lg shadow-lg font-medium"
          >
            <Link to="/home" className="inline-flex items-center gap-2">
              <Home className="w-4 h-4" />
              Return Home
            </Link>
          </Button>

          {is404 ? (
            <Button
              asChild
              variant="outline"
              size="lg"
              className="border-zinc-700 bg-zinc-900/60 hover:bg-zinc-800 text-white rounded-lg shadow-lg inline-flex items-center gap-2"
            >
              <Link to="/products" className="inline-flex items-center gap-2">
                <ArrowLeft className="w-4 h-4" />
                Browse Catalog
              </Link>
            </Button>
          ) : (
            <Button
              variant="outline"
              size="lg"
              onClick={() => window.location.reload()}
              className="border-zinc-700 bg-zinc-900/60 hover:bg-zinc-800 text-white rounded-lg shadow-lg inline-flex items-center gap-2 cursor-pointer"
            >
              <RefreshCw className="w-4 h-4" />
              Reload Page
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}
