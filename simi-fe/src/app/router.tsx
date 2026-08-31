import { createBrowserRouter } from "react-router";
import AuthLayout from "../layouts/AuthLayout";
import LoginPage from "../feature/auth/pages/LoginPage";
import RegisterPage from "../feature/auth/pages/RegisterPage";
import { MainLayout } from "../layouts/MainLayout";
import { HomePage } from "../feature/home/pages/HomePage";
import { ProductDetailPage } from "../feature/product/pages/ProductDetailPage";
import { ProductListPage } from "../feature/product/pages/ProductListPage";
import { CheckoutPage } from "../feature/order/pages/CheckoutPage";
import { ProfilePage } from "../feature/user/pages/ProfilePage";
import { StaffLayout } from "../layouts/StaffLayout";
import { StaffOrderListPage } from "../feature/staff/order/pages/StaffOrderListPage";
import { StaffConsignmentPage } from "../feature/staff/consignment/pages/StaffConsignmentPage";
import { StaffPOSPage } from "../feature/staff/pos/pages/StaffPOSPage";
import { StaffConsignmentDetailPage } from "../feature/staff/consignment/pages/StaffConsignmentDetailPage";
import { CartPage } from "../feature/cart/page/CartPage";
import { MyOrders } from "../feature/user/components/MyOrders";
import { MyConsignment } from "../feature/user/components/MyConsignment";
import { MyConsignmentDetailPage } from "../feature/consignment/pages/MyConsignmentDetailPage";
import { PaymentResultPage } from "../feature/order/pages/PaymentResultPage";
import { OrderSuccessPage } from "../feature/order/pages/OrderSuccessPage";
import { StaffDashboard } from "../feature/staff/dashboard/pages/StaffDashboard";
import { StaffDispositionQueuePage } from "../feature/disposition/pages/StaffDispositionQueuePage";
import { AboutPage } from "../feature/about/pages/AboutPage";

export const router = createBrowserRouter([
  {
    element: <AuthLayout />,
    children: [
      {
        path: "/login",
        element: <LoginPage />,
      },
      {
        path: "/register",
        element: <RegisterPage />,
      },
    ],
  },
  {
    element: <MainLayout />,
    children: [
      {
        path: "/",
        element: <HomePage />,
      },
      {
        path: "/products",
        element: <ProductListPage />,
      },
      {
        path: "/about",
        element: <AboutPage />,
      },
      {
        path: "/products/:id",
        element: <ProductDetailPage />,
      },
      {
        path: "/checkout",
        element: <CheckoutPage />,
      },
      {
        path: "/profile",
        element: <ProfilePage />,
      },
      {
        path: "/cart",
        element: <CartPage />,
      },
      {
        path: "/my-orders",
        element: <MyOrders />,
      },
      {
        path: "/my-consignments",
        element: <MyConsignment />,
      },
      {
        path: "/my-consignments/:id",
        element: <MyConsignmentDetailPage />,
      },
      {
        path: "/payment/result",
        element: <PaymentResultPage />,
      },
      {
        path: "/orders/success",
        element: <OrderSuccessPage />,
      },
    ],
  },
  {
    element: <StaffLayout />,
    children: [
      {
        path: "/staff/dashboard",
        element: <StaffDashboard />,
      },
      {
        path: "/staff/orders",
        element: <StaffOrderListPage />,
      },
      {
        path: "/staff/consignments",
        element: <StaffConsignmentPage />,
      },
      {
        path: "/staff/consignments/:id",
        element: <StaffConsignmentDetailPage />,
      },
      {
        path: "/staff/pos",
        element: <StaffPOSPage />,
      },
      {
        path: "/staff/dispositions",
        element: <StaffDispositionQueuePage />,
      },
    ],
  },
]);
