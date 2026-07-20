import { create } from "zustand";
import type { UserResponse } from "../types/user";
import { userApi } from "../feature/user/api/userApi";

interface AuthState{
    user: UserResponse | null;
    accessToken: string | null;
    isInitialized: boolean;

    setAuth: (user: UserResponse, accessToken: string) => void;
    initialize: () => Promise<void>;
    clearAuth: () => void;
    isAuthenticated: () => boolean;
    isAdmin: () => boolean;
    isStaff: () => boolean;
    isCustomer: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
    user: null,
    accessToken: localStorage.getItem("accessToken"),
    isInitialized: false,

    setAuth: (user, token) => {
        localStorage.setItem('accessToken', token)
        set({user: user, accessToken: token})
    },
    initialize: async () => {
        const token = get().accessToken

        if(!token){
            set({isInitialized: true})
            return;
        }

        try{
            const res = await userApi.getMe()
            set({user: res.data.body, isInitialized: true})
        }catch{
            localStorage.removeItem('accessToken');
            set({user: null, isInitialized: true, accessToken: null})
        }
    },
    clearAuth: () => {
        localStorage.removeItem('accessToken')
        set({
            user: null,
            isInitialized: true,
            accessToken: null
        })
    },
    isAuthenticated: () => !!get().accessToken,
    isAdmin: () => get().user?.role == "ADMIN",
    isStaff: () => get().user?.role == "STAFF",
    isCustomer: () => get().user?.role == "CUSTOMER"
}))