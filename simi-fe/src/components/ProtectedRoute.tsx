import { Navigate, Outlet } from "react-router";
import { useAuthStore } from "../store/useAuthStore";

interface ProtectedRouteProps{
    allowedRoles: "STAFF" | "ADMIN";
    redirectTo?: string;
}

export const ProtectedRoute = ({allowedRoles, redirectTo = "/login"}: ProtectedRouteProps) => {
    const {user, accessToken} = useAuthStore();

    if (!accessToken || !user){
        return <Navigate to={redirectTo} replace/>
    }

    if(!allowedRoles.includes(user.role)){
        const ROLE_REDIRECT_MAP: Record<string, string> = {
            CUSTOMER: "/",
            STAFF: "/staff",
            ADMIN: "/admin"
        };

        const targetPath = ROLE_REDIRECT_MAP[user.role] || "/";
        return <Navigate to={targetPath} replace/>
    }

    return <Outlet/>
}