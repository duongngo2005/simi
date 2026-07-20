import api from "../../../lib/http/apiClient"
import type { ApiResponse } from "../../../types/common"
import type { UserResponse } from "../../../types/user"

export const userApi = {
    getMe: () => api.get<ApiResponse<UserResponse>>('/users/me')
}