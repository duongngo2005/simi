import api from "../../../lib/http/apiClient";
import { type AuthTokenResponse, type LoginRequest, type RegisterRequest } from "../../../types/auth";
import type { ApiResponse } from "../../../types/common";

export const authApi = {
    login: (data: LoginRequest) => api.post<ApiResponse<AuthTokenResponse>>("/auth/login", data),
    
    register: (data: RegisterRequest) => api.post<ApiResponse<AuthTokenResponse>>("/auth/register", data),

    refresh: () => api.post<ApiResponse<AuthTokenResponse>>("/auth/refresh"),

    logout: () => api.post("/auth/logout")
}