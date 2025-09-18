import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, createRoutesFromElements, RouterProvider, Route } from "react-router-dom";
import "./index.css";
import App from "./App.tsx";
import ErrorPage from "./components/ErrorPage.tsx";
import Home from "./components/Home.tsx";
import About from "./components/About.tsx";
import Contact from "./components/Contact.tsx";
import Login from "./components/Login.tsx";


const routeDefinitions = createRoutesFromElements(
  <Route path="/" element= {<App/>} errorElement={<ErrorPage/>}>
    <Route path="/home" element={<Home/>}/>
    <Route path="/about" element={<About/>}/>
    <Route path="/contact" element={<Contact/>}/>
    <Route path="/login" element={<Login/>}/>
  </Route>
)

const appRouter = createBrowserRouter(routeDefinitions);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={appRouter}/>
  </StrictMode>
);
