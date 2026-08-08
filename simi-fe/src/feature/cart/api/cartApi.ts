import api from "../../../lib/http/apiClient";
import type { ApiResponse } from "../../../types/common";
import type { CartResponse } from "../type/cart.type";

export const cartApi = {
    getMyCart: async () => {
        const response = await api.get<ApiResponse<CartResponse>>("/carts");
        return response.data
    },
    removeItem: async (id: number) => {
        await api.delete(`/carts/remove/${id}`);
    }
}