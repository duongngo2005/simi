import { createBrowserRouter } from "react-router";
import AuthLayout from "../layouts/AuthLayout";
import LoginPage from "../feature/auth/pages/LoginPage";
import RegisterPage from "../feature/auth/pages/RegisterPage";
import { MainLayout } from "../layouts/MainLayout";
import { HomePage } from "../feature/home/pages/HomePage";
import { ProductDetailPage } from "../feature/product/pages/ProductDetailPage";
import { CheckoutPage } from "../feature/order/pages/CheckoutPage";
import { ProfilePage } from "../feature/user/pages/ProfilePage";
import { StaffLayout } from "../layouts/StaffLayout";
import { StaffDashboard } from "../feature/staff/order/pages/StaffDashboard";
import { StaffOrderListPage } from "../feature/staff/order/pages/StaffOrderListPage";
import { StaffConsignmentPage } from "../feature/staff/consignment/pages/StaffConsignmentPage";
import { StaffPOSPage } from "../feature/staff/pos/pages/StaffPOSPage";

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
            },
            {
                path: "/checkout",
                element: <CheckoutPage/>
            },
            {
                path: "/profile",
                element: <ProfilePage/>
            }
        ]
    },
    {
        element: <StaffLayout/>,
        children: [
            {
                path: "/staff/dashboard",
                element: <StaffDashboard/>
            },
            {
                path: "/staff/orders",
                element: <StaffOrderListPage/>
            },
            {
                path: "/staff/consignments",
                element: <StaffConsignmentPage/>
            },
            {
                path: "/staff/pos",
                element: <StaffPOSPage/>
            }
        ]
    }
])