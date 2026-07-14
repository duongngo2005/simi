import { QueryClientProvider } from "@tanstack/react-query";
import type { ReactNode } from "react";
import { queryClient } from "./queryClient";
import AuthProvider from "../feature/auth/provider/AuthProvider";

interface AppProvidersProps{
    children: ReactNode
}

export default function AppProviders({
    children
}: AppProvidersProps){
    return (
        <QueryClientProvider client={queryClient}>
            <AuthProvider>
                {children}
            </AuthProvider>
        </QueryClientProvider>
    )
}