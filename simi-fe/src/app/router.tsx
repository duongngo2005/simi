import { createBrowserRouter } from "react-router";
import AuthLayout from "../layouts/AuthLayout";
import LoginPage from "../feature/auth/pages/LoginPage";
import RegisterPage from "../feature/auth/pages/RegisterPage";
import { MainLayout } from "../layouts/MainLayout";
import { HomePage } from "../feature/home/pages/HomePage";
import { ProductDetailPage } from "../feature/product/pages/ProductDetailPage";

export const router = createBrowserRouter([
    {
        element: <AuthLayout/>,
        children: [
            {
                path: '/login',
                element: <LoginPage/>
            },
            {
                path: '/register',
                element: <RegisterPage/>
            }
        ]
    }, 
    {
        element: <MainLayout/>,
        children: [
            {
                path: "/",
                element: <HomePage/>
            },
            {
                path: "/products/:id",
                element: <ProductDetailPage/>
            }
        ]
    }
])