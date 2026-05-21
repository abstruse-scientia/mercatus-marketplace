import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import {
  createBrowserRouter,
  createRoutesFromElements,
  RouterProvider,
  Route,
  Navigate,
} from "react-router-dom";
import "./index.css";
import App from "./App.tsx";
import ErrorPage from "./components/ErrorPage.tsx";
import Home from "./components/Home.tsx";
import About from "./components/About.tsx";
import Contact from "./components/Contact.tsx";
import Login from "./components/Login.tsx";
import Register from "./components/Register.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";

const routeDefinitions = createRoutesFromElements(
  <Route path="/" element={<App />} errorElement={<ErrorPage />}>
    <Route index element={<Navigate to="/home" />} />
    <Route path="/home" element={<Home />} />
    <Route path="/about" element={<About />} />

    <Route path="/contact" element={<Contact />} />
    <Route path="/login" element={<Login />} />
    <Route path="/register" element={<Register />} />

    {/* Protected Routes */}
    <Route element={<ProtectedRoute />}>
      <Route
        path="/cart"
        element={<div className="p-8 text-center">Cart Placeholder</div>}
      />
      <Route
        path="/orders"
        element={<div className="p-8 text-center">Orders Placeholder</div>}
      />
      <Route
        path="/checkout"
        element={<div className="p-8 text-center">Checkout Placeholder</div>}
      />
      <Route
        path="/account/*"
        element={<div className="p-8 text-center">Account Placeholder</div>}
      />
    </Route>
  </Route>,
);

const appRouter = createBrowserRouter(routeDefinitions);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={appRouter} />
  </StrictMode>,
);
