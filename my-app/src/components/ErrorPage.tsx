import errorImage from "../assets/images/error-page-pic.png";
import { Button } from "./ui/button";
import { Link } from "react-router-dom";

export default function ErrorPage() {
  return (
    <div
      className="min-h-screen bg-gray-900 bg-cover bg-center bg-no-repeat flex items-center justify-start p-4 sm:p-8 relative"
      style={{
        // First layer: gradient, second layer: image resolved by Vite
        backgroundImage: `linear-gradient(to right, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.8) 50%, rgba(0,0,0,0.4) 100%), url(${errorImage})`,
      }}
    >
      <div className="max-w-lg relative z-10 w-full sm:w-auto">
        <h1 className="text-6xl sm:text-7xl md:text-8xl lg:text-9xl font-bold text-red-500 font-mono leading-none mb-3 sm:mb-4 drop-shadow-2xl">
          404
        </h1>

        <h2 className="text-xl sm:text-2xl md:text-3xl font-medium text-white mb-6 sm:mb-8 drop-shadow-lg">
          Oops...page not found
        </h2>

        <p className="text-gray-100 text-base sm:text-lg leading-relaxed mb-6 sm:mb-8 max-w-md drop-shadow-md">
          The page you're looking for has vanished like an old photograph.
          Perhaps it was never developed.
        </p>

        <div className="flex flex-col gap-3 sm:gap-4 w-full sm:w-auto sm:flex-row">
          <Button
            asChild
            size="lg"
            className="bg-white text-gray-900 hover:bg-gray-100 drop-shadow-lg"
          >
            <Link to="/home">Return Home</Link>
          </Button>
        </div>
      </div>
    </div>
  );
}
