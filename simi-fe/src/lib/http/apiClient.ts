import axios from 'axios';
import type { ApiResponse } from '../../types/common';
import type { AuthTokenResponse } from '../../types/auth';
import { useAuthStore } from '../../store/useAuthStore';

const api = axios.create({
    baseURL: "http://localhost:8080/api/v1/",
    withCredentials: true
})

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');

    if(token){
        config.headers.Authorization = `Bearer ${token}`
    }
    
    return config;
})

let refreshing: Promise<void> | null = null;

api.interceptors.response.use( 
    (response) => response,
    async (error) => {
        const originalRequest = error.config

        if (error.response?.status !== 401 || originalRequest._retry){
            return Promise.reject(error);
        }

        originalRequest._retry = true;

        if (!refreshing){
            refreshing = api
            .post<ApiResponse<AuthTokenResponse>>('/auth/refresh')
            .then((res) => {
                localStorage.setItem('accessToken', res.data.body.accessToken);
            })
            .catch(() => {
                useAuthStore.getState().clearAuth();
                window.location.href = "/login";
            })
            .finally(() => {
                refreshing = null;
            })
        }

        await refreshing;

        const newToken = localStorage.getItem("accessToken");
        if(newToken){
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return api(originalRequest);
        }

        return Promise.reject(error);
    }
)

export default api;