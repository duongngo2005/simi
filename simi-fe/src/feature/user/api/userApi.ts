import api from "../../../lib/http/apiClient"
import type { ApiResponse } from "../../../types/common"
import type { UserResponse } from "../../../types/user"

export const userApi = {
    getMe: () => api.get<ApiResponse<UserResponse>>('/users/me'),
    updateBankInformation: async (request: {
        bankName: string;
        accountNumber: string;
        accountHolder: string;
    }) => {
        const response = await api.patch<ApiResponse<UserResponse>>('/users/me/bank-information', request);
        return response.data;
    }
}
