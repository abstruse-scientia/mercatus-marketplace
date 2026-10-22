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
import ForgotPassword from "./components/ForgotPassword.tsx";
import ProductDetail from "./components/ProductDetail.tsx";
import ProductsPage from "./components/ProductsPage.tsx";
import Cart from "./components/Cart.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";
import Checkout from "./components/Checkout.tsx";
import OrderConfirmation from "./components/OrderConfirmation.tsx";

import Orders from "./components/Orders.tsx";
import AccountLayout from "./components/AccountLayout.tsx";
import AddressBook from "./components/AddressBook.tsx";

// Admin Imports
import AdminProtectedRoute from "./components/admin/AdminProtectedRoute.tsx";
import AdminLayout from "./components/admin/AdminLayout.tsx";
import AdminProducts from "./components/admin/AdminProducts.tsx";
import AdminProductForm from "./components/admin/AdminProductForm.tsx";
import AdminCategories from "./components/admin/AdminCategories.tsx";
import AdminOrders from "./components/admin/AdminOrders.tsx";

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
    <Route path="/forgot-password" element={<ForgotPassword/>}/>

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
      <Route path="/account" element={<AccountLayout />}>
        <Route index element={<Navigate to="addresses" replace />} />
        <Route path="addresses" element={<AddressBook />} />
        <Route path="orders" element={<Orders />} />
      </Route>
    </Route>

    {/* Admin Routes */}
    <Route element={<AdminProtectedRoute />}>
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<Navigate to="products" replace />} />
        <Route path="products" element={<AdminProducts />} />
        <Route path="products/new" element={<AdminProductForm />} />
        <Route path="products/:id" element={<AdminProductForm />} />
        <Route path="categories" element={<AdminCategories />} />
        <Route path="orders" element={<AdminOrders />} />
      </Route>
    </Route>
  </Route>,
);

const appRouter = createBrowserRouter(routeDefinitions);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={appRouter} />
  </StrictMode>,
);
