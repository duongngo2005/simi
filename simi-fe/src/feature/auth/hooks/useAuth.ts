import { useNavigate } from "react-router"
import { useAuthStore } from "../../../store/useAuthStore"
import type {LoginRequest, RegisterRequest } from "../../../types/auth"
import { authApi } from "../apis/auth.api"
import { useMutation } from "@tanstack/react-query" 

export const useLogin = () => {
    const setAuth = useAuthStore((s) => s.setAuth)
    const navigate = useNavigate()

    return useMutation({
        mutationFn: (data: LoginRequest) => authApi.login(data),
        onSuccess: (res) => {
            const {accessToken, userResponse} = res.data.body
            if (!accessToken) return;
            setAuth(userResponse, accessToken)
            navigate("/")
        }
    })
}

export const useRegister = () => {
    const setAuth = useAuthStore((s) => s.setAuth)
    const navigate = useNavigate()

    return useMutation({
        mutationFn: (data: RegisterRequest) => authApi.register(data),
        onSuccess: (res) => {
            const {accessToken, userResponse} = res.data.body
            if(accessToken){
                setAuth(userResponse, accessToken)
                navigate("/")
            }
        }
    })
}

export const useLogout = () => {
    const navigate = useNavigate()
    const clearAuth = useAuthStore((s) => s.clearAuth)

    return useMutation({
        mutationFn: () => authApi.logout(),
        onSettled: () => {
            clearAuth()
            navigate('/login')
        }
    })
}