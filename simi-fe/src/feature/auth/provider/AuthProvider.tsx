import { useEffect, type ReactNode } from "react";
import { useAuthStore } from "../../../store/useAuthStore";

interface AuthProviderProps{
    children: ReactNode
}

export default function AuthProvider ({
    children
}: AuthProviderProps){
    const initialize = useAuthStore(s => s.initialize);
    const initialized = useAuthStore(s => s.isInitialized);

    useEffect(() => {
        initialize()
    }, [initialize])

    if(!initialized){
        return(
            <div >
                <span>Loading...</span>
            </div>
        )
    }
    
    return <>{children}</>;
}