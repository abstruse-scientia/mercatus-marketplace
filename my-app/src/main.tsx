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
import ProductDetail from "./components/ProductDetail.tsx";
import ProductsPage from "./components/ProductsPage.tsx";
import Cart from "./components/Cart.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";
import Checkout from "./components/Checkout.tsx";
import OrderConfirmation from "./components/OrderConfirmation.tsx";

import Orders from "./components/Orders.tsx";

const routeDefinitions = createRoutesFromElements(
  <Route path="/" element={<App />} errorElement={<ErrorPage />}>
    <Route index element={<Navigate to="/home" />} />
    <Route path="/home" element={<Home />} />
    <Route path="/products" element={<ProductsPage />} />
    <Route path="/products/:id" element={<ProductDetail />} />
    <Route path="/about" element={<About />} />

    <Route path="/contact" element={<Contact />} />
    <Route path="/login" element={<Login />} />
    <Route path="/register" element={<Register />} />

    <Route path="/cart" element={<Cart />} />

    {/* Protected Routes */}
    <Route element={<ProtectedRoute />}>
      <Route
        path="/orders"
        element={<Orders />}
      />
      <Route
        path="/orders/:orderId"
        element={<OrderConfirmation />}
      />
      <Route
        path="/checkout"
        element={<Checkout />}
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
