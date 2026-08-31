import { useEffect, type ReactNode } from "react";
import { useAuthStore } from "../../../store/useAuthStore";
import styles from "./AuthProvider.module.css";

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
            <div className={styles.loadingShell} role="status" aria-live="polite">
                <span className={styles.loadingMark} aria-hidden="true" />
                <span>Đang khôi phục phiên đăng nhập…</span>
            </div>
        )
    }
    
    return <>{children}</>;
}
